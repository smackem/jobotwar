using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Jobotwar.WebApp.Features.Api;
using Jobotwar.WebApp.Services;

namespace Jobotwar.WebApp
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            var builder = WebAssemblyHostBuilder.CreateDefault(args);
            builder.RootComponents.Add<App>("#app");

            Console.WriteLine($"base address: {builder.HostEnvironment.BaseAddress}");

            ConfigureServices(builder);

            await builder.Build().RunAsync();
        }

        private static void ConfigureServices(WebAssemblyHostBuilder builder)
        {
            var baseUri = new Uri(builder.HostEnvironment.BaseAddress);
            var apiUri = new UriBuilder(baseUri) { Port = 8666 }.Uri;
            builder.Services.AddLogging();
            builder.Services.AddHttpClient("wwwroot", client =>
            {
                client.BaseAddress = baseUri;
            });
            builder.Services.AddHttpClient<IApiClient, ApiClient>(client =>
            {
                client.BaseAddress = apiUri;
            });
            builder.Services.AddScoped(sp => new TickerFactory());
        }
    }
}
