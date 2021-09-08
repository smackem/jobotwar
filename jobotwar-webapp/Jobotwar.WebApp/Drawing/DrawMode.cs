using System;

namespace Jobotwar.WebApp.Drawing
{
    [Flags]
    internal enum DrawMode
    {
        Fill = 1,
        Stroke = 2,
        FillAndStroke = 3,
    }
}
