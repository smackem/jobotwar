using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Blazor.Extensions.Canvas.Canvas2D;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Services;
using Jobotwar.WebApp.Util;

namespace Jobotwar.WebApp.Drawing
{
    internal class MatchReplay
    {
        private readonly int _boardWidth;
        private readonly int _boardHeight;
        private readonly Canvas2DContext _gc;
        private readonly InstantMatchResult _match;
        private readonly IEnumerator<MatchFrame> _frameEnumerator;
        private readonly List<FadingRadarBeam> _fadingRadarBeams = new();

        private MatchReplay(int boardWidth, int boardHeight, InstantMatchResult match, Canvas2DContext gc)
        {
            _boardWidth = boardWidth;
            _boardHeight = boardHeight;
            _gc = gc;
            _match = match;
            _frameEnumerator = match.Frames.GetEnumerator();
        }

        public static Task Play(int boardWidth, int boardHeight, InstantMatchResult match, Canvas2DContext gc, TickerFactory tickerFactory)
        {
            var replay = new MatchReplay(boardWidth, boardHeight, match, gc);
            return tickerFactory.Repeat(replay.Tick, TimeSpan.FromMilliseconds(40));
        }

        private async Task<bool> Tick()
        {
            if (_frameEnumerator.MoveNext() == false)
            {
                return false;
            }
            await RenderFrame(_gc, _frameEnumerator.Current).Return();
            return true;
        }

        private async Task RenderFrame(Canvas2DContext gc, MatchFrame frame)
        {
            const double robotRadius = 18;
            const double projectileRadius = 3;
            await gc.SetFillStyleAsync("#000000").Continue();
            await gc.FillRectAsync(0, 0, _boardWidth, _boardHeight).Continue();

            await gc.BeginBatchAsync().Continue();
            await gc.SetLineWidthAsync(1).Continue();
            foreach (var fadingRadarBeam in _fadingRadarBeams)
            {
                await DrawRadarBeam(gc, fadingRadarBeam).Continue();
                fadingRadarBeam.Opacity -= 0.1;
            }
            _fadingRadarBeams.RemoveAll(x => x.Opacity <= 0);
            await gc.EndBatchAsync().Continue();

            await gc.BeginBatchAsync().Continue();
            foreach (var radarBeam in frame.RadarBeams)
            {
                var fadingRadarBeam = new FadingRadarBeam(radarBeam);
                await DrawRadarBeam(gc, fadingRadarBeam).Continue();
                _fadingRadarBeams.Add(fadingRadarBeam);
            }
            await gc.EndBatchAsync().Continue();

            await gc.BeginBatchAsync().Continue();
            await gc.SetFillStyleAsync("#ffffff").Continue();
            foreach (var (x, y) in frame.Projectiles)
            {
                await DrawCircle(gc, x, y, projectileRadius, false).Continue();
            }
            await gc.EndBatchAsync().Continue();

            await gc.BeginBatchAsync().Continue();
            await gc.SetFillStyleAsync("#c000a0").Continue();
            await gc.SetStrokeStyleAsync("#ffffff").Continue();
            await gc.SetLineWidthAsync(2).Continue();
            foreach (var (_, x, y) in frame.Robots)
            {
                await DrawCircle(gc, x, y, robotRadius, true).Continue();
            }
            await gc.EndBatchAsync().Continue();
        }

        private static async Task DrawRadarBeam(Canvas2DContext gc, FadingRadarBeam fadingRadarBeam)
        {
            var radarBeam = fadingRadarBeam.RadarBeam;
            await gc.SetStrokeStyleAsync($"rgba(255, {0xaa}, 00, {fadingRadarBeam.Opacity})").Continue();
            await gc.BeginPathAsync().Continue();
            await gc.MoveToAsync(radarBeam.X1, radarBeam.Y1).Continue();
            await gc.LineToAsync(radarBeam.X2, radarBeam.Y2).Continue();
            await gc.StrokeAsync().Continue();
        }

        private static async Task DrawCircle(Canvas2DContext gc, double x, double y, double radius, bool stroke)
        {
            await gc.BeginPathAsync().Continue();
            await gc.ArcAsync(x, y, radius, 0, 2 * Math.PI, false).Continue();
            await gc.FillAsync().Continue();
            if (stroke)
            {
                await gc.StrokeAsync().Continue();
            }
        }

        private record FadingRadarBeam
        {
            public FadingRadarBeam(RadarBeamVisual radarBeam)
            {
                RadarBeam = radarBeam;
                Opacity = radarBeam.Kind switch
                {
                    "WALL" => 0.4,
                    "ROBOT" => 0.9,
                    _ => throw new ArgumentException("Invalid radar beam hit kind!"),
                };
            }

            public RadarBeamVisual RadarBeam { get; }
            public double Opacity { get; set; }
        }
    }
}
