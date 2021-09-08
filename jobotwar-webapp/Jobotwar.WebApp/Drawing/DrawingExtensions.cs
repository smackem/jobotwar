using System.Drawing;

namespace Jobotwar.WebApp.Drawing
{
    internal static class ColorExtensions
    {
        public static string ToCssRgba(this Color rgba)
        {
            return $"rgba({rgba.R}, {rgba.G}, {rgba.B}, {rgba.A / 255.0})";
        }
    }
}