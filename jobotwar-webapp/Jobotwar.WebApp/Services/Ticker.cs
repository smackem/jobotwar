using System;
using System.Threading;

namespace Jobotwar.WebApp.Services
{
    internal class Ticker : IDisposable
    {
        private readonly Timer _timer;
        private readonly Func<bool> _tick;
        private volatile bool _disposed;

        public Ticker(int intervalMillis, Func<bool> tick)
        {
            if (intervalMillis < 0)
            {
                throw new ArgumentOutOfRangeException(nameof(intervalMillis));
            }

            _tick = tick;
            _timer = new Timer(TimerTick, null, intervalMillis, intervalMillis);
        }

        public TimeSpan Interval { get; }

        public bool IsDisposed => _disposed;

        public void Dispose()
        {
            _timer?.Dispose();
            _disposed = true;
        }

        private void TimerTick(object? state)
        {
            if (_tick() == false)
            {
                Dispose();
            }
        }
    }
}
