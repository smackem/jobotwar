using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Threading;
using Microsoft.JSInterop;
using Blazor.Extensions.Canvas.Canvas2D;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Services;

namespace Jobotwar.WebApp.Drawing
{
    internal class MatchReplay
    {
        private readonly Canvas2DContext _gc;
        private readonly MatchInfo _match;
        private readonly GameInfo _gameInfo;
        private readonly IEnumerator<MatchFrame> _frameEnumerator;
        private readonly List<AnimatedRadarBeam> _animatedRadarBeams = new();
        private readonly List<AnimatedExplosion> _animatedExplosions = new();

        private MatchReplay(MatchInfo match, GameInfo gameInfo, Canvas2DContext gc)
        {
            _gc = gc;
            _match = match;
            _gameInfo = gameInfo;
            _frameEnumerator = match.Result.Frames.GetEnumerator();
        }

        public static Task Play(MatchInfo match, GameInfo gameInfo, Canvas2DContext gc, TickerFactory tickerFactory, CancellationToken cancellationToken)
        {
            var replay = new MatchReplay(match, gameInfo, gc);
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
                await RenderFrame(_gc, _frameEnumerator.Current);
            }
            catch (JSException)
            {
                // rendering failed because canvas has become invalid (e.g. when user has navigated to another page)
                return false;
            }

            return true;
        }

        private async Task RenderFrame(Canvas2DContext gc, MatchFrame frame)
        {
            const double projectileRadius = 3;
            await gc.SetFillStyleAsync("#000000");
            await gc.FillRectAsync(0, 0, _match.Setup.BoardWidth, _match.Setup.BoardHeight);

            await gc.BeginBatchAsync();
            await gc.SetLineWidthAsync(1);
            foreach (var animatedRadarBeam in _animatedRadarBeams)
            {
                await DrawRadarBeamAsync(gc, animatedRadarBeam);
                animatedRadarBeam.Tick();
            }
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            foreach (var radarBeam in frame.RadarBeams)
            {
                var animatedRadarBeam = new AnimatedRadarBeam(radarBeam);
                await DrawRadarBeamAsync(gc, animatedRadarBeam);
                _animatedRadarBeams.Add(animatedRadarBeam);
            }
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            await gc.SetFillStyleAsync("#ffffff");
            foreach (var (x, y) in frame.Projectiles)
            {
                await DrawCircleAsync(gc, x, y, projectileRadius, DrawMode.Fill);
            }
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            await gc.SetStrokeStyleAsync("#ffffff");
            await gc.SetLineWidthAsync(2);
            foreach (var (name, x, y) in frame.Robots)
            {
                await gc.SetFillStyleAsync(_match.RobotInfos[name].Rgba.ToCssRgba());
                await DrawCircleAsync(gc, x, y, _gameInfo.RobotRadius, DrawMode.FillAndStroke);
            }
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            await gc.SetLineWidthAsync(8);
            foreach (var animatedExplosion in _animatedExplosions)
            {
                await gc.SetStrokeStyleAsync($"rgba(255, 40, 0, {animatedExplosion.Opacity})");
                await DrawCircleAsync(gc, animatedExplosion.Explosion.X, animatedExplosion.Explosion.Y, animatedExplosion.Radius, DrawMode.Stroke);
                animatedExplosion.Tick();
            }
            await gc.EndBatchAsync();

            foreach (var explosion in frame.Explosions)
            {
                _animatedExplosions.Add(new AnimatedExplosion(explosion, _gameInfo.ExplosionRadius));
            }

            _animatedRadarBeams.RemoveAll(x => x.IsAnimationFinished);
            _animatedExplosions.RemoveAll(x => x.IsAnimationFinished);
        }

        private static async Task DrawRadarBeamAsync(Canvas2DContext gc, AnimatedRadarBeam fadingRadarBeam)
        {
            var radarBeam = fadingRadarBeam.RadarBeam;
            await gc.SetStrokeStyleAsync($"rgba(255, {0xaa}, 00, {fadingRadarBeam.Opacity})");
            await gc.BeginPathAsync();
            await gc.MoveToAsync(radarBeam.X1, radarBeam.Y1);
            await gc.LineToAsync(radarBeam.X2, radarBeam.Y2);
            await gc.StrokeAsync();
        }

        private static async Task DrawCircleAsync(Canvas2DContext gc, double x, double y, double radius, DrawMode drawMode)
        {
            await gc.BeginPathAsync();
            await gc.ArcAsync(x, y, radius, 0, 2 * Math.PI, false);
            if ((drawMode & DrawMode.Fill) != 0)
            {
                await gc.FillAsync();
            }
            if ((drawMode & DrawMode.Stroke) != 0)
            {
                await gc.StrokeAsync();
            }
        }
    }
}
