using System;
using Jobotwar.WebApp.Features.Api;

namespace Jobotwar.WebApp.Drawing
{
    internal class AnimatedExplosion : IAnimated
    {
        public AnimatedExplosion(ExplosionVisual explosion, double finalRadius)
        {
            Explosion = explosion;
            FinalRadius = finalRadius;
            Opacity = 1.0;
        }

        public ExplosionVisual Explosion { get; }
        public double FinalRadius { get; }
        public double Opacity { get; private set; }
        public double Radius { get; private set; }

        public bool IsAnimationFinished => Radius >= FinalRadius;

        public void Tick()
        {
            Opacity = Math.Max(0.0, Opacity - 0.1);
            Radius = Math.Min(FinalRadius, Radius + 4);
        }
   }
}