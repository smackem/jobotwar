using System;
using System.Threading;

namespace Jobotwar.WebApp.Services
{
    class Ticker
    {
        private Timer? _timer;

        public TimeSpan Interval { get; init; } = TimeSpan.FromMilliseconds(100);

        event EventHandler? Tick;

        public void Start()
        {
            _timer = new Timer(_ => Tick?.Invoke(this, EventArgs.Empty), null, (int) Interval.TotalMilliseconds, (int) Interval.TotalMilliseconds);
        }

        public void Stop()
        {
            _timer?.Dispose();
        }
    }
}