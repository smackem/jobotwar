using System;
using System.Collections.Generic;
using Jobotwar.WebApp.Shared;
using Microsoft.Extensions.Logging;

namespace Jobotwar.WebApp.Services
{
    public class ModelContainer : IModelContainer
    {
        private readonly ILogger<ModelContainer> _log;
        private static readonly Random Rand = new();
        private static readonly string[] Colors = new[]
        {
            "#ff3333",
            "#ff9933",
            "#ffff33",
            "#99ff33",
            "#33ff33",
            "#33ff99",
            "#33ffff",
            "#3399ff",
            "#3333ff",
            "#9933ff",
            "#ff33ff",
            "#ff3399",
        };

        public ModelContainer(ILogger<ModelContainer> log)
        {
            _log = log;
            Robots = new List<RobotModel>
            {
                new()
                {
                    Name = "Fast Shooter ",
                    Color = "#ff8040",
                    Code = @"state main() {
    def angle
    @speed(@random(-50, 50), @random(-50, 50))
    while true {
        def r = @radar(angle)
        if r < 0 {
            @fire(angle, abs(r))
        } else {
            angle = angle + 7
        }
    }
}",
                },
                new()
                {
                    Name = "Slow Shooter",
                    Color = "#4080ff",
                    Code = @"def angle = 0
state main() {
    @speed(@random(-50, 50), @random(-50, 50))
    def r = @radar(angle)
    if r < 0 {
        @fire(angle, abs(r))
    } else {
        angle = angle + 7
    }
}",
                },
            };
        }

        public ICollection<RobotModel> Robots { get; }

        public RobotModel NewRobot()
        {
            var robot = new RobotModel
            {
                Name = "Robot " + (Robots.Count + 1),
                Color = Colors[Rand.Next(Colors.Length)],
            };
            _log.LogInformation("created new robot: {NewRobot}", robot);
            Robots.Add(robot);
            return robot;
        }

        public void RemoveRobot(RobotModel robot)
        {
            Robots.Remove(robot);
        }
    }
}