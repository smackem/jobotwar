using System.Collections.Generic;
using System.Threading.Tasks;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Shared;

namespace Jobotwar.WebApp.Services
{
    public interface IPlayService
    {
        int BoardWidth { get; }
        int BoardHeight { get; }

        Task<(InstantMatchSetup, InstantMatchResult)> PlayMatchAsync(IEnumerable<RobotModel> robots);

        Task<IEnumerable<InstantMatchResult>> SimulateMatchesAsync(IEnumerable<RobotModel> robots, int matchCount);
    }
}
