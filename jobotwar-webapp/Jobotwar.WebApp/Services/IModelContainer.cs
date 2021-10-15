using System.Collections.Generic;
using System.Collections.Immutable;
using Jobotwar.WebApp.Shared;

namespace Jobotwar.WebApp.Services
{
    public interface IModelContainer
    {
        public IReadOnlyCollection<RobotModel> Robots { get; }

        public RobotModel NewRobot();

        public void RemoveRobot(RobotModel robot);
    }
}