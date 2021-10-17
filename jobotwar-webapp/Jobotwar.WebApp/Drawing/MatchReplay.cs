using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Threading;
using Microsoft.JSInterop;
using Blazor.Extensions.Canvas.Canvas2D;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Services;
using Microsoft.Extensions.Logging;

namespace Jobotwar.WebApp.Drawing
{
    internal class MatchReplay
    {
        private readonly Canvas2DContext _gc;
        private readonly MatchInfo _match;
        private readonly GameInfo _gameInfo;
        private readonly ILogger<MatchReplay> _log;
        private readonly IEnumerator<MatchFrame> _frameEnumerator;
        private readonly List<AnimatedRadarBeam> _animatedRadarBeams = new();
        private readonly List<AnimatedExplosion> _animatedExplosions = new();

        private MatchReplay(MatchInfo match, GameInfo gameInfo, Canvas2DContext gc, ILogger<MatchReplay> log)
        {
            _gc = gc;
            _match = match;
            _gameInfo = gameInfo;
            _frameEnumerator = match.Result.Frames.GetEnumerator();
            _log = log;
        }

        public static Task PlayAsync(MatchInfo match,
            GameInfo gameInfo,
            Canvas2DContext gc,
            TickerFactory tickerFactory,
            ILogger<MatchReplay> log,
            CancellationToken cancellationToken)
        {
            var replay = new MatchReplay(match, gameInfo, gc, log);
            return tickerFactory.Repeat(replay.Tick, TimeSpan.FromMilliseconds(gameInfo.FrameDurationMillis), cancellationToken);
        }

        private async Task<bool> Tick()
        {
            if (_frameEnumerator.MoveNext() == false)
            {
                return false;
            }

            try
            {
                await RenderFrame(_frameEnumerator.Current);
            }
            catch (JSException)
            {
                // rendering failed because canvas has become invalid (e.g. when user has navigated to another page)
                return false;
            }

            return true;
        }

        private async Task RenderFrame(MatchFrame frame)
        {
            await _gc.BeginBatchAsync();
            await _gc.SetFillStyleAsync("#000000");
            await _gc.FillRectAsync(0, 0, _match.Setup.BoardWidth, _match.Setup.BoardHeight);
            await DrawRadarBeamsAsync(frame.RadarBeams, frame);
            await DrawProjectilesAsync(frame.Projectiles);
            await DrawRobotsAsync(frame.Robots);
            await DrawExplosionsAsync(frame.Explosions);
            await _gc.EndBatchAsync();
        }

        private async Task DrawRadarBeamsAsync(IEnumerable<RadarBeamVisual> radarBeams, MatchFrame currentFrame)
        {
            foreach (var animatedRadarBeam in _animatedRadarBeams)
            {
                await DrawRadarBeamAsync(animatedRadarBeam, currentFrame);
                animatedRadarBeam.Tick();
            }

            foreach (var radarBeam in radarBeams)
            {
                var animatedRadarBeam = new AnimatedRadarBeam(radarBeam);
                await DrawRadarBeamAsync(animatedRadarBeam, currentFrame);
                _animatedRadarBeams.Add(animatedRadarBeam);
            }

            _animatedRadarBeams.RemoveAll(x => x.IsAnimationFinished);
        }
        
        private async Task DrawProjectilesAsync(IEnumerable<ProjectileVisual> projectiles)
        {
            const double projectileRadius = 3;
            await _gc.SetFillStyleAsync("#ffffff");
            foreach (var (x, y) in projectiles)
            {
                await DrawCircleAsync(x, y, projectileRadius, DrawMode.Fill);
            }
        }

        private async Task DrawRobotsAsync(IEnumerable<RobotVisual> robots)
        {
            foreach (var robot in robots)
            {
                await _gc.SetFillStyleAsync("#303030");
                await DrawCircleAsync(robot.X, robot.Y, _gameInfo.RobotRadius, DrawMode.Fill);

                await _gc.SetLineWidthAsync(2);
                var healthRatio = robot.Health / 100.0;
                var angle = healthRatio * 2.0 * Math.PI;
                var hue = healthRatio * 120.0;
                await _gc.SetStrokeStyleAsync($"hsl({hue}, 75%, 50%)");
                await _gc.BeginPathAsync();
                await _gc.ArcAsync(robot.X, robot.Y, _gameInfo.RobotRadius, 0, angle, false);
                await _gc.StrokeAsync();

                await _gc.SetLineWidthAsync(1);
                await _gc.SetStrokeStyleAsync("#000000");
                await _gc.SetFillStyleAsync(_match.RobotInfos[robot.Name].CssColor);
                await DrawCircleAsync(robot.X, robot.Y, _gameInfo.RobotRadius - 2, DrawMode.FillAndStroke);
            }
        }

        private async Task DrawExplosionsAsync(IEnumerable<ExplosionVisual> explosions)
        {
            await _gc.SetLineWidthAsync(8);
            foreach (var animatedExplosion in _animatedExplosions)
            {
                await _gc.SetStrokeStyleAsync($"rgba(255, 40, 0, {animatedExplosion.Opacity})");
                await DrawCircleAsync(animatedExplosion.Explosion.X, animatedExplosion.Explosion.Y, animatedExplosion.Radius, DrawMode.Stroke);
                animatedExplosion.Tick();
            }

            _animatedExplosions.RemoveAll(x => x.IsAnimationFinished);

            foreach (var explosion in explosions)
            {
                _animatedExplosions.Add(new AnimatedExplosion(explosion, _gameInfo.ExplosionRadius));
            }
        }

        private async Task DrawRadarBeamAsync(AnimatedRadarBeam beam, MatchFrame currentFrame)
        {
            var emittingRobot =
                (from r in currentFrame.Robots
                where r.Name == beam.RadarBeam.EmittingRobotName
                select r)
                .FirstOrDefault();
            if (emittingRobot == null)
            {
                return;
            }

            var color = _match.RobotInfos[emittingRobot.Name].CssColor;
            var radarBeam = beam.RadarBeam;
            await _gc.SetLineWidthAsync(radarBeam.HitKind == "WALL" ? 1 : 2);
            var (red, green, blue) = Rgba.ParseCssColor(color);
            await _gc.SetStrokeStyleAsync($"#{red:X2}{green:X2}{blue:X2}{(int) (beam.Opacity * 255):X2}");
            await _gc.BeginPathAsync();
            await _gc.MoveToAsync(emittingRobot.X, emittingRobot.Y);
            await _gc.LineToAsync(radarBeam.HitX, radarBeam.HitY);
            await _gc.StrokeAsync();
        }

        private async Task DrawCircleAsync(double x, double y, double radius, DrawMode drawMode)
        {
            await _gc.BeginPathAsync();
            await _gc.ArcAsync(x, y, radius, 0, 2 * Math.PI, false);
            if ((drawMode & DrawMode.Fill) != 0)
            {
                await _gc.FillAsync();
            }
            if ((drawMode & DrawMode.Stroke) != 0)
            {
                await _gc.StrokeAsync();
            }
        }
    }
}
