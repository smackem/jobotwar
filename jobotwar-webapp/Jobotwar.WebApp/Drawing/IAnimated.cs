namespace Jobotwar.WebApp.Drawing
{
    internal interface IAnimated
    {
        bool IsAnimationFinished { get; }
        void Tick();
    }
}