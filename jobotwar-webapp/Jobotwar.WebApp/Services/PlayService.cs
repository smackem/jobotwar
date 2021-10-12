using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Shared;

namespace Jobotwar.WebApp.Services
{
    internal class PlayService : IPlayService
    {
        private static readonly Random Rand = new();
        private readonly IApiClient _api;
        private const int PlacementMargin = 30;
        
        public PlayService(IApiClient api)
        {
            _api = api;
        }

        public int BoardWidth => 640;

        public int BoardHeight => 480;

        public async Task<(InstantMatchSetup, InstantMatchResult)> PlayMatchAsync(IEnumerable<RobotModel> robots)
        {
            return await InternalPlayMatchAsync(robots, excludeFrames: false);
        }

        public async Task<IEnumerable<InstantMatchResult>> SimulateMatchesAsync(IEnumerable<RobotModel> robots, int matchCount)
        {
            var tasks = Enumerable.Range(0, matchCount)
                .Select(_ => InternalPlayMatchAsync(robots, excludeFrames: true));
            var tuples = await Task.WhenAll(tasks);
            return tuples.Select(t => t.Item2);
        }

        private async Task<(InstantMatchSetup, InstantMatchResult)> InternalPlayMatchAsync(IEnumerable<RobotModel> robots, bool excludeFrames)
        {
            const int margin = 30;
            var setup = new InstantMatchSetup(5 * 60 * 1000, BoardWidth, BoardHeight,
                robots.Select(r => new InstantMatchRobot(r.Name, r.Code, r.LanguageVersion,
                    Rand.Next(PlacementMargin, BoardWidth - margin),
                    Rand.Next(margin, BoardHeight - margin)))
                    .ToArray(),
                excludeFrames);
            var result = await _api.PlayAsync(setup);
            return (setup, result);
        }
    }
}
