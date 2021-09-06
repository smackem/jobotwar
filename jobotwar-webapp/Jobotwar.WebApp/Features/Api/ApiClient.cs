using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Json;

namespace Jobotwar.WebApp.Features.Api
{
    internal class ApiClient : IApiClient
    {
        private readonly HttpClient _http;

        public ApiClient(HttpClient http)
        {
            _http = http;
        }

        public async Task<InstantMatchResult> PlayAsync(InstantMatchSetup setup)
        {
            var response = await _http.PostAsJsonAsync("/play", setup);
            var result = await response.Content.ReadFromJsonAsync<InstantMatchResult>();
            return result!;
        }

        public async Task<GameInfo?> GetGameInfoAsync()
        {
            return await _http.GetFromJsonAsync<GameInfo>("/info");
        }
    }
}
