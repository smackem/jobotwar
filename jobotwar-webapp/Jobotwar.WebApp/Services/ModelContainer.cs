using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using Jobotwar.WebApp.Shared;
using Microsoft.Extensions.Logging;

namespace Jobotwar.WebApp.Services
{
    public class ModelContainer : IModelContainer
    {
        private readonly ILogger<ModelContainer> _log;
        private readonly Random _random;
        private readonly List<RobotModel> _robots;
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

        public ModelContainer(ILogger<ModelContainer> log, Random random)
        {
            _log = log;
            _random = random;
            _robots = new List<RobotModel>
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

        public IReadOnlyCollection<RobotModel> Robots => new ReadOnlyCollection<RobotModel>(_robots);

        public RobotModel NewRobot()
        {
            var robot = new RobotModel
            {
                Name = "Robot " + (Robots.Count + 1),
                Color = Colors[_random.Next(Colors.Length)],
            };
            _log.LogInformation("created new robot: {NewRobot}", robot);
            _robots.Add(robot);
            return robot;
        }

        public void RemoveRobot(RobotModel robot)
        {
            _robots.Remove(robot);
        }
    }
}