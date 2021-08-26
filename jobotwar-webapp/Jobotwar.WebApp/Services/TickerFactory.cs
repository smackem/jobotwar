using System;
using System.Collections.Generic;

namespace Jobotwar.WebApp.Services
{
    internal class TickerFactory : IDisposable
    {
        private readonly object _monitor = new object();
        private readonly List<Ticker> _tickers = new List<Ticker>();

        public Ticker CreateTicker(int intervalMillis, Func<bool> tick)
        {
            var ticker = new Ticker(intervalMillis, tick);

            lock (_monitor)
            {
                _tickers.RemoveAll(ticker => ticker.IsDisposed);
                _tickers.Add(ticker);
            }

            return ticker;
        }

        public void Dispose()
        {
            lock (_monitor)
            {
                foreach (var ticker in _tickers)
                {
                    ticker.Dispose();
                }

                _tickers.Clear();
            }
        }
    }
}
