using System;
using System.Net.Http;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Text;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using System.Threading;
using Jobotwar.WebApp.Services;

namespace Jobotwar.WebApp
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            var builder = WebAssemblyHostBuilder.CreateDefault(args);
            builder.RootComponents.Add<App>("#app");

            System.Console.WriteLine($"base address: {builder.HostEnvironment.BaseAddress}");

            ConfigureServices(builder);

            var cts = new CancellationTokenSource();

            await Task.WhenAll(
                Task.Run(async () =>
                {
                    while (cts.Token.IsCancellationRequested == false)
                    {
                        await Task.Delay(1000, cts.Token);
                        Console.WriteLine($"tick: {System.Environment.TickCount}");
                    }
                }),
                builder.Build().RunAsync()).ContinueWith(t => cts.Cancel());
        }

        private static void ConfigureServices(WebAssemblyHostBuilder builder)
        {
            var baseUri = new Uri(builder.HostEnvironment.BaseAddress);
            var apiUri = new UriBuilder(baseUri) { Port = 8666 }.Uri;

            builder.Services.AddScoped(sp => new HttpClientProvider(
                new HttpClient { BaseAddress = baseUri },
                new HttpClient { BaseAddress = apiUri }));

            builder.Services.AddScoped(sp => new TickerFactory());
        }
    }
}
