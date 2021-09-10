using System;
using System.Collections.Generic;
using System.Drawing;
using System.Threading.Tasks;
using System.Threading;
using Microsoft.JSInterop;
using Blazor.Extensions.Canvas.Canvas2D;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Services;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace Jobotwar.WebApp.Drawing
{
    internal class MatchReplay
    {
        private readonly Canvas2DContext _gc;
        private readonly MatchInfo _match;
        private readonly GameInfo _gameInfo;
        private readonly ILogger _log;
        private readonly IEnumerator<MatchFrame> _frameEnumerator;
        private readonly List<AnimatedRadarBeam> _animatedRadarBeams = new();
        private readonly List<AnimatedExplosion> _animatedExplosions = new();

        private MatchReplay(MatchInfo match, GameInfo gameInfo, Canvas2DContext gc, ILogger log)
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
            ILogger log,
            CancellationToken cancellationToken)
        {
            var replay = new MatchReplay(match, gameInfo, gc, log);
            return tickerFactory.Repeat(replay.Tick, TimeSpan.FromMilliseconds(40), cancellationToken);
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
            await _gc.SetFillStyleAsync("#000000");
            await _gc.FillRectAsync(0, 0, _match.Setup.BoardWidth, _match.Setup.BoardHeight);

            await DrawProjectilesAsync(frame.Projectiles);
            await DrawRadarBeamsAsync(frame.RadarBeams);
            await DrawRobotsAsync(frame.Robots);
            await DrawExplosionsAsync(frame.Explosions);
        }

        private async Task DrawRadarBeamsAsync(IEnumerable<RadarBeamVisual> radarBeams)
        {
            await _gc.BeginBatchAsync();
            await _gc.SetLineWidthAsync(1);
            foreach (var animatedRadarBeam in _animatedRadarBeams)
            {
                await DrawRadarBeamAsync(animatedRadarBeam);
                animatedRadarBeam.Tick();
            }
            await _gc.EndBatchAsync();

            await _gc.BeginBatchAsync();
            foreach (var radarBeam in radarBeams)
            {
                var animatedRadarBeam = new AnimatedRadarBeam(radarBeam);
                await DrawRadarBeamAsync(animatedRadarBeam);
                _animatedRadarBeams.Add(animatedRadarBeam);
            }
            await _gc.EndBatchAsync();

            _animatedRadarBeams.RemoveAll(x => x.IsAnimationFinished);
        }
        
        private async Task DrawProjectilesAsync(IEnumerable<ProjectileVisual> projectiles)
        {
            const double projectileRadius = 3;
            await _gc.BeginBatchAsync();
            await _gc.SetFillStyleAsync("#ffffff");
            foreach (var (x, y) in projectiles)
            {
                await DrawCircleAsync(x, y, projectileRadius, DrawMode.Fill);
            }
            await _gc.EndBatchAsync();
        }

        private async Task DrawRobotsAsync(IEnumerable<RobotVisual> robots)
        {
            await _gc.BeginBatchAsync();
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
                await _gc.SetFillStyleAsync(_match.RobotInfos[robot.Name].Rgba.ToCssRgba());
                await DrawCircleAsync(robot.X, robot.Y, _gameInfo.RobotRadius - 2, DrawMode.FillAndStroke);
            }
            await _gc.EndBatchAsync();
        }

        private async Task DrawExplosionsAsync(IEnumerable<ExplosionVisual> explosions)
        {
            await _gc.BeginBatchAsync();
            await _gc.SetLineWidthAsync(8);
            foreach (var animatedExplosion in _animatedExplosions)
            {
                await _gc.SetStrokeStyleAsync($"rgba(255, 40, 0, {animatedExplosion.Opacity})");
                await DrawCircleAsync(animatedExplosion.Explosion.X, animatedExplosion.Explosion.Y, animatedExplosion.Radius, DrawMode.Stroke);
                animatedExplosion.Tick();
            }
            await _gc.EndBatchAsync();

            _animatedExplosions.RemoveAll(x => x.IsAnimationFinished);

            foreach (var explosion in explosions)
            {
                _animatedExplosions.Add(new AnimatedExplosion(explosion, _gameInfo.ExplosionRadius));
            }
        }

        private async Task DrawRadarBeamAsync(AnimatedRadarBeam fadingRadarBeam)
        {
            var radarBeam = fadingRadarBeam.RadarBeam;
            await _gc.SetStrokeStyleAsync($"rgba(255, {0xaa}, 00, {fadingRadarBeam.Opacity})");
            await _gc.BeginPathAsync();
            await _gc.MoveToAsync(radarBeam.X1, radarBeam.Y1);
            await _gc.LineToAsync(radarBeam.X2, radarBeam.Y2);
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
