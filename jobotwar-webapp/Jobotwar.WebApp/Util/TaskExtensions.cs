using System.Runtime.CompilerServices;
using System.Threading.Tasks;

namespace Jobotwar.WebApp.Util
{
    public static class TaskExtensions
    {
        public static ConfiguredTaskAwaitable Continue(this Task task)
        {
            return task.ConfigureAwait(false);
        }

        public static ConfiguredTaskAwaitable Return(this Task task)
        {
            return task.ConfigureAwait(true);
        }
    }
}
