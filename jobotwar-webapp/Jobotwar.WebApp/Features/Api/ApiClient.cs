using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Json;
using Microsoft.Extensions.Logging;

namespace Jobotwar.WebApp.Features.Api
{
    internal class ApiClient : IApiClient
    {
        private readonly HttpClient _http;
        private readonly ILogger<ApiClient> _log;
        private GameInfo? _cachedGameInfo;

        public ApiClient(HttpClient http, ILogger<ApiClient> log)
        {
            _http = http;
            _log = log;
        }

        public async Task<InstantMatchResult> PlayAsync(InstantMatchSetup setup)
        {
            var response = await _http.PostAsJsonAsync("/play", setup);
            var result = await response.Content.ReadFromJsonAsync<InstantMatchResult>();
            return result!;
        }

        public async Task<GameInfo> GetGameInfoAsync()
        {
            if (_cachedGameInfo == null)
            {
                _cachedGameInfo = await _http.GetFromJsonAsync<GameInfo>("/info");
                _log.LogInformation("got game info: {GameInfo}", _cachedGameInfo);
            }
            return _cachedGameInfo!;
        }
    }
}
