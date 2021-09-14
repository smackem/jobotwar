using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Jobotwar.WebApp.Util;

namespace Jobotwar.WebApp.Services
{
    internal class TickerFactory : IDisposable
    {
        private readonly object _monitor = new();
        private readonly List<CancellationTokenSource> _cancellationTokenSources = new();

        public async Task Repeat(Func<Task<bool>> tick, TimeSpan interval, CancellationToken cancellationToken)
        {
            var cts = new CancellationTokenSource();
            _cancellationTokenSources.Add(cts);
            while (cts.IsCancellationRequested == false && cancellationToken.IsCancellationRequested == false)
            {
                await Task.Delay(interval, cts.Token).Continue();
                if (await tick() == false)
                {
                    break;
                }
            }
        }

        public void Dispose()
        {
            lock (_monitor)
            {
                foreach (var cts in _cancellationTokenSources)
                {
                    cts.Cancel();
                }

                _cancellationTokenSources.Clear();
            }
        }
    }
}
