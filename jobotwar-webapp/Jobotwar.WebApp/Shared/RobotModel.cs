namespace Jobotwar.WebApp.Shared
{
    public class RobotModel
    {
        public string Name { get; set; } = string.Empty;

        public string Code { get; set; } = @"state main() {
}
";

        public string LanguageVersion { get; set; } = "V2";

        public string Color { get; set; } = "#ff0000";

        public override string ToString()
        {
            return $"{nameof(Name)}: {Name}, {nameof(Code)}: {Code}, {nameof(LanguageVersion)}: {LanguageVersion}, {nameof(Color)}: {Color}";
        }
    }
}
