using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Microsoft.AspNetCore.Components.Rendering;

// ReSharper disable ClassNeverInstantiated.Global

namespace Jobotwar.WebApp.Features.Api
{
    public record InstantMatchSetup(
        int MaxDurationMillis,
        double BoardWidth,
        double BoardHeight,
        ICollection<InstantMatchRobot> Robots,
        bool ExcludeFrames);

    public record InstantMatchRobot(
        string Name,
        string Code,
        string Language,
        double X,
        double Y);

    public record InstantMatchResult(
        string Outcome,
        string? Winner,
        int DurationMillis,
        ICollection<MatchEvent> EventLog,
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
        double Y,
        int Health,
        double AimAngle);

    public record ProjectileVisual(
        double X,
        double Y);

    public record ExplosionVisual(
        double X,
        double Y,
        string Kind);

    public record RadarBeamVisual(
        string EmittingRobotName,
        double HitX,
        double HitY,
        string HitKind);

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

    public record CompileRequest(
        string RobotName,
        string Language,
        string Code);

    public record CompileResult(
        string Program);

    public record Robot(
        string Code,
        string Language,
        string Name,
        double Acceleration,
        int Rgb,
        DateTimeOffset DateCreated,
        DateTimeOffset? DateModified);
}
