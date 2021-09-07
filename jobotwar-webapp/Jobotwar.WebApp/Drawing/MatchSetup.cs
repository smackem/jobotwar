using System.Collections.Immutable;
using Jobotwar.WebApp.Features.Api;

namespace Jobotwar.WebApp.Drawing
{
    public record MatchSetup(
        InstantMatchSetup Model,
        IImmutableDictionary<string, RobotDrawingInfo> RobotInfos);

    public record RobotDrawingInfo(string Color);
}
