using System.Net.Http;

namespace Jobotwar.WebApp.Services
{
    record HttpClientProvider(HttpClient WwwRoot, HttpClient Api);
}