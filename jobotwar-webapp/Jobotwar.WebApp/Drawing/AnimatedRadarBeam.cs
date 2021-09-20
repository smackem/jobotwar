using System;
using Jobotwar.WebApp.Features.Api;

namespace Jobotwar.WebApp.Drawing
{
    internal class AnimatedRadarBeam : IAnimated
    {
        public AnimatedRadarBeam(RadarBeamVisual radarBeam)
        {
            RadarBeam = radarBeam;
            Opacity = radarBeam.HitKind switch
            {
                "WALL" => 0.4,
                "ROBOT" => 0.9,
                _ => throw new ArgumentException($"Invalid radar beam hit kind '{radarBeam.HitKind}'!"),
            };
        }

        public RadarBeamVisual RadarBeam { get; }
        public double Opacity { get; private set; }

        public bool IsAnimationFinished => Opacity <= 0;

        public void Tick()
        {
            Opacity = Math.Max(0, Opacity - 0.05);
        }
    }
}
