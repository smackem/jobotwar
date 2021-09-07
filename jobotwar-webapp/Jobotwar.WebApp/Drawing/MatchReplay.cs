using System;
using System.Collections.Generic;
using System.Drawing;
using System.Threading.Tasks;
using Blazor.Extensions.Canvas.Canvas2D;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Services;
using Jobotwar.WebApp.Util;

namespace Jobotwar.WebApp.Drawing
{
    internal class MatchReplay
    {
        private readonly Canvas2DContext _gc;
        private readonly MatchInfo _match;
        private readonly IEnumerator<MatchFrame> _frameEnumerator;
        private readonly List<FadingRadarBeam> _fadingRadarBeams = new();

        private MatchReplay(MatchInfo match, Canvas2DContext gc)
        {
            _gc = gc;
            _match = match;
            _frameEnumerator = match.Result.Frames.GetEnumerator();
        }

        public static Task Play(MatchInfo match, Canvas2DContext gc, TickerFactory tickerFactory)
        {
            var replay = new MatchReplay(match, gc);
            return tickerFactory.Repeat(replay.Tick, TimeSpan.FromMilliseconds(40));
        }

        private async Task<bool> Tick()
        {
            if (_frameEnumerator.MoveNext() == false)
            {
                return false;
            }
            await RenderFrame(_gc, _frameEnumerator.Current);
            return true;
        }

        private async Task RenderFrame(Canvas2DContext gc, MatchFrame frame)
        {
            const double robotRadius = 18;
            const double projectileRadius = 3;
            await gc.SetFillStyleAsync("#000000");
            await gc.FillRectAsync(0, 0, _match.Setup.BoardWidth, _match.Setup.BoardHeight);

            await gc.BeginBatchAsync();
            await gc.SetLineWidthAsync(1);
            foreach (var fadingRadarBeam in _fadingRadarBeams)
            {
                await DrawRadarBeam(gc, fadingRadarBeam);
                fadingRadarBeam.Opacity -= 0.1;
            }
            _fadingRadarBeams.RemoveAll(x => x.Opacity <= 0);
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            foreach (var radarBeam in frame.RadarBeams)
            {
                var fadingRadarBeam = new FadingRadarBeam(radarBeam);
                await DrawRadarBeam(gc, fadingRadarBeam);
                _fadingRadarBeams.Add(fadingRadarBeam);
            }
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            await gc.SetFillStyleAsync("#ffffff");
            foreach (var (x, y) in frame.Projectiles)
            {
                await DrawCircle(gc, x, y, projectileRadius, false);
            }
            await gc.EndBatchAsync();

            await gc.BeginBatchAsync();
            await gc.SetStrokeStyleAsync("#ffffff");
            await gc.SetLineWidthAsync(2);
            foreach (var (name, x, y) in frame.Robots)
            {
                await gc.SetFillStyleAsync(ToFillStyle(_match.RobotInfos[name].Rgba));
                await DrawCircle(gc, x, y, robotRadius, true);
            }
            await gc.EndBatchAsync();
        }

        private static string ToFillStyle(Color rgba)
        {
            return $"rgba({rgba.R}, {rgba.G}, {rgba.B}, {rgba.A / 255.0})";
        }

        private static async Task DrawRadarBeam(Canvas2DContext gc, FadingRadarBeam fadingRadarBeam)
        {
            var radarBeam = fadingRadarBeam.RadarBeam;
            await gc.SetStrokeStyleAsync($"rgba(255, {0xaa}, 00, {fadingRadarBeam.Opacity})");
            await gc.BeginPathAsync();
            await gc.MoveToAsync(radarBeam.X1, radarBeam.Y1);
            await gc.LineToAsync(radarBeam.X2, radarBeam.Y2);
            await gc.StrokeAsync();
        }

        private static async Task DrawCircle(Canvas2DContext gc, double x, double y, double radius, bool stroke)
        {
            await gc.BeginPathAsync();
            await gc.ArcAsync(x, y, radius, 0, 2 * Math.PI, false);
            await gc.FillAsync();
            if (stroke)
            {
                await gc.StrokeAsync();
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
