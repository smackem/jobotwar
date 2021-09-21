using System.Collections.Generic;
using Jobotwar.WebApp.Shared;

namespace Jobotwar.WebApp.Services
{
    public interface IModelContainer
    {
        public ICollection<RobotModel> Robots { get; }

        public RobotModel NewRobot();

        public void RemoveRobot(RobotModel robot);
    }
}