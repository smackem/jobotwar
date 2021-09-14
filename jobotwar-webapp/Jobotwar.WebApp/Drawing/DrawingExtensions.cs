using System.Drawing;
using System.Globalization;
using System.Text.RegularExpressions;

namespace Jobotwar.WebApp.Drawing
{
    internal static class Rgba
    {
        public static string ToCssRgba(this Color rgba)
        {
            return $"rgba({rgba.R}, {rgba.G}, {rgba.B}, {rgba.A / 255.0})";
        }

        public static string ToCssRgba(this Color rgba, double opacity)
        {
            return $"rgba({rgba.R}, {rgba.G}, {rgba.B}, {opacity})";
        }

        public static (byte r, byte g, byte b) ParseCssColor(string cssColor)
        {
            var match = Regex.Match(cssColor, @"\#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})");
            return (byte.Parse(match.Groups[1].Value, NumberStyles.HexNumber),
                byte.Parse(match.Groups[2].Value, NumberStyles.HexNumber),
                byte.Parse(match.Groups[3].Value, NumberStyles.HexNumber));
        }
    }
}