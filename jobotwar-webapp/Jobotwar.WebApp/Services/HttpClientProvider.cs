using System;
using System.Net.Http;

namespace Jobotwar.WebApp.Services
{
    internal record HttpClientProvider(HttpClient WwwRoot, HttpClient Api) : IDisposable
    {
        public void Dispose()
        {
            WwwRoot.Dispose();
            Api.Dispose();
        }
    }
}
