using System.Collections.Immutable;
using System.Drawing;
using Jobotwar.WebApp.Features.Api;

namespace Jobotwar.WebApp.Drawing
{
    public record MatchInfo(
        InstantMatchSetup Setup,
        InstantMatchResult Result,
        IImmutableDictionary<string, RobotDrawingInfo> RobotInfos);

    public record RobotDrawingInfo(Color Rgba);
}
