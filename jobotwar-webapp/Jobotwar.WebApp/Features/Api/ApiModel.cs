﻿using System.Collections.Generic;
// ReSharper disable ClassNeverInstantiated.Global

namespace Jobotwar.WebApp.Features.Api
{
    public record InstantMatchSetup(
        int MaxDurationMillis,
        double BoardWidth,
        double BoardHeight,
        ICollection<InstantMatchRobot> Robots);

    public record InstantMatchRobot(
        string Name,
        string Code,
        string Language,
        double X,
        double Y);

    public record InstantMatchResult(
        string Outcome,
        string Winner,
        int DurationMillis,
        ICollection<MatchEvent> Events,
        ICollection<MatchFrame> Frames);

    public record MatchEvent(
        int GameTimeMillis,
        string Event);

    public record MatchFrame(
        int GameMillis,
        ICollection<RobotVisual> Robots,
        ICollection<ProjectileVisual> Projectiles,
        ICollection<ExplosionVisual> Explosions,
        ICollection<RadarBeamVisual> RadarBeams);

    public record RobotVisual(
        string Name,
        double X,
        double Y);

    public record ProjectileVisual(
        double X,
        double Y);

    public record ExplosionVisual(
        double X,
        double Y,
        string Kind);

    public record RadarBeamVisual(
        double X1,
        double Y1,
        double X2,
        double Y2,
        string Kind);

    public record GameInfo(
        double ExplosionRadius,
        double FrameDurationMillis,
        string GameVersion,
        double MaxBoardHeight,
        double MaxBoardWidth,
        double MaxRobotAcceleration,
        int MaxRobotHealth,
        double MaxRobotSpeed,
        double RobotRadius);
}
