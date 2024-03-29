﻿using System.Collections.Generic;
using System.Threading.Tasks;

namespace Jobotwar.WebApp.Features.Api
{
    internal interface IApiClient
    {
        Task<InstantMatchResult> PlayAsync(InstantMatchSetup setup);

        Task<GameInfo> GetGameInfoAsync();

        Task<CompileResult> Compile(CompileRequest request);

        Task<IEnumerable<Robot>> GetRobotsAsync();
    }
}
