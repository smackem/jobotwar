using System.Collections.Generic;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Logging;

namespace Jobotwar.WebApp.Features.Api
{
    internal class ApiClient : IApiClient
    {
        private readonly HttpClient _http;
        private readonly ILogger<ApiClient> _log;
        private readonly IMemoryCache _cache;

        public ApiClient(HttpClient http, ILogger<ApiClient> log, IMemoryCache cache)
        {
            _http = http;
            _log = log;
            _cache = cache;
        }

        public async Task<InstantMatchResult> PlayAsync(InstantMatchSetup setup)
        {
            const string uri = "/play";
            var response = await _http.PostAsJsonAsync(uri, setup);
            if (response.IsSuccessStatusCode == false)
            {
                throw new BadRequestException(uri, response.StatusCode, await response.Content.ReadAsStringAsync());
            }

            // var json = await response.Content.ReadAsStringAsync();
            // _log.LogInformation("received JSON {JSON}", json);
            //var result = JsonSerializer.Deserialize<InstantMatchResult>(json);
            var result = await response.Content.ReadFromJsonAsync<InstantMatchResult>();
            return result!;
        }

        public Task<GameInfo> GetGameInfoAsync()
        {
            return _cache.GetOrCreateAsync("GameInfo",
                async _ =>
                {
                    var gameInfo = await _http.GetFromJsonAsync<GameInfo>("/info");
                    _log.LogInformation("got game info: {GameInfo}", gameInfo);
                    return gameInfo!;
                });
        }

        public async Task<CompileResult> Compile(CompileRequest request)
        {
            const string uri = "/compile";
            var response = await _http.PostAsJsonAsync(uri, request);
            if (response.IsSuccessStatusCode == false)
            {
                throw new BadRequestException(uri, response.StatusCode, await response.Content.ReadAsStringAsync());
            }

            var result = await response.Content.ReadFromJsonAsync<CompileResult>();
            return result!;
        }

        public async Task<IEnumerable<Robot>> GetRobotsAsync()
        {
            const string uri = "/robot";
            var robots = await _http.GetFromJsonAsync<ICollection<Robot>>(uri);
            return robots!;
        }
    }
}
