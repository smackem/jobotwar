using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Json;
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
            var response = await _http.PostAsJsonAsync("/play", setup);
            var result = await response.Content.ReadFromJsonAsync<InstantMatchResult>();
            return result!;
        }

        public Task<GameInfo> GetGameInfoAsync()
        {
            return _cache.GetOrCreateAsync("GameInfo", async entry =>
            {
                var gameInfo = await _http.GetFromJsonAsync<GameInfo>("/info");
                _log.LogInformation("got game info: {GameInfo}", gameInfo);
                return gameInfo!;
            });
        }
    }
}
