$random = [System.Random]::new()
$circler = ConvertFrom-Json (get-content ./src/site/circler.jobot)
$batteringram = ConvertFrom-Json (get-content ./src/site/batteringramV2.jobot)

function match1v1([string] $robot1Name, [string] $robot1, [string] $robot2Name, [string] $robot2) {
    return @{
        maxDurationMillis = 1000*60*5
        boardWidth = 640
        boardHeight = 480
        robots = @(
            @{
                name = $robot1Name
                code = $robot1.code
                language = $robot1.language
                x = $random.Next(40, 600)
                y = $random.Next(40, 440)
            },
            @{
                name = $robot2Name
                code = $robot2.code
                language = $robot2.language
                x = $random.Next(40, 600)
                y = $random.Next(40, 440)
            }
        )
    }
}