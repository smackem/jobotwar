INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('9e0c2c53-96da-49a3-acbf-56db9f91c662', 'Bumblebee++', 'V2', '// robot ''bumblebee''
// moves fast from random location to random location, evading fire.
// does not shoot at all but relies on other robots to kill each other

state main() {
    def destX = @random(50, 590)
    def destY = @random(50, 430)
    @log(destX)
    @log(destY)
    @speed(destX - @x(), destY - @y())
    yield move(destX, destY)
}

state move(destX, destY) {
    if hypot(destX - @x(), destY - @y()) < 50 {
        yield main()
    }
}
', 1, 0, '2021-02-16 13:12:56.208504', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('594c6ab0-da49-401a-a883-5b2f5d6b986f', 'Shooter++', 'V2', '// robot ''shooter++''
// v2 version of the classic shooter
state main() {
    def angle
    // use a loop instead of re-iterating the state. its much faster.
    while true {
        def r = @radar(angle)
        if r < 0 {
            @fire(angle, abs(r))
        } else {
            angle = angle + 7
        }
    }
}
', 1, 0, '2021-02-16 13:12:56.369345', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('3fc309a7-ee7c-4f52-8ede-324d98db528e', 'Patrol++', 'V2', '// Robot Patrol
// moves to the left or right side of the board - whichever is closer
// then patrols from top to bottom and back, shooting across the board
// performs well in battles with more than 2 robots and against
// 360 deg scanning robots

state main() {
    if @x() < 320 {
        yield moveToLeft()
    } else {
        yield moveToRight()
    }
}

state moveToLeft() {
    @speedX(-200)
    @fire(0, 1000)
    if @x() < 50 {
        @speedX(0)
        yield patrol(0, 200, 90)
    }
}

state moveToRight() {
    @speedX(200)
    @fire(180, 1000)
    if @x() > 590 {
        @speedX(0)
        yield patrol(180, 200, 90)
    }
}

state patrol(fireAngle, speedY, radarAngle) {
    @speedY(speedY)
    def r = @radar(radarAngle)
    if r < 60 {
        @log(r)
        yield patrol(
            fireAngle,
            0 - speedY + @random(-10, 10),
            radarAngle + 180)
    }
    @fire(fireAngle, 1000)
}
', 1, 0, '2021-02-16 13:12:56.384671', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('2484d1bb-cff0-4a8c-af1e-6f8eb9de95bb', 'Shooter', 'V1', '// robot ''shooter''
// stationary robot that scans its surroundings 360 degrees and
// shoots if it detects a robots.
// its power is simplicity and fast scanning.

loop:
    AIM + 7 -> AIM -> RADAR
    goto loop unless RADAR < 0
shoot:
    0 - RADAR -> SHOT -> OUT
    AIM -> RADAR
    goto shoot if RADAR < 0
    goto loop
', 1, 0, '2021-02-16 13:12:56.402390', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('bad556ca-1d21-49d9-b879-68698129085e', 'Corner++', 'V2', '// Robot Corner
// moves to the closest corner, then scans the board (only 90 deg needed)
// performs excellent in battles with more than 2 robots

def corner = -1

state main() {
    if @x() < 320 and @y() < 240 {
        yield topLeft()
    } else if @x() >= 320 and @y() < 240 {
        yield topRight()
    } else if @x() < 320 and @y() > 240 {
        yield bottomLeft()
    } else { //if X > 320 and Y > 240
        yield bottomRight()
    }
}

state topLeft() {
    corner = 3
    @log(corner)
    yield move(25, 25, 0)
}

state topRight() {
    corner = 2
    @log(corner)
    yield move(615, 25, 90)
}

state bottomLeft() {
    corner = 1
    @log(corner)
    yield move(25, 455, 270)
}

state bottomRight() {
    corner = 0
    @log(corner)
    yield move(615, 455, 180)
}

state move(destX, destY, angleBase) {
    // move to corner, shooting backwards
    def dx = destX - @x(), dy = destY - @y()
    @speed(dx, dy)
    def tailGunAngle = getBackwardAngle()
    @fire(tailGunAngle + @random(-30, 30), 1000)
    if hypot(dx, dy) < 30 {
        @speed(0, 0)
        yield scan(@damage(), angleBase)
    }
}

def getBackwardAngle() {
    def angle = atan(@speedY() / @speedX())
    if @speedX() > 0 {
        angle = angle + 180
    }
    return angle
}

state changeCorner() {
    // pick a new random corner, make sure it is not the current one
    def newCorner = corner
    while newCorner == corner {
        newCorner = trunc(@random(4))
    }
    @log(newCorner)
    // set destination to the new corner
    corner = newCorner
    if corner == 3 {
        yield topLeft()
    } else if corner == 2 {
        yield topRight()
    } else if corner == 1 {
        yield bottomLeft()
    } else {
        yield bottomRight()
    }
}

// scan the board, monitoring the damage
state scan(damage, angleBase) {
    def angle = 0
    while true {
        def a = angleBase - 15 + angle
        def r = @radar(a)
        if r < 0 {
            @fire(a, abs(r))
        } else {
            angle = (angle + 7) % 120
        }
        // change corner if damage taken
        if damage != @damage() {
            yield changeCorner()
        }
    }
}
', 1, 0, '2021-02-16 13:12:56.429392', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('04b73638-191c-4b3e-9f1a-b6f36c287c27', 'target', 'V1', '', 1, 0, '2021-02-16 13:12:56.208500', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('3dc3de7e-1695-4757-84c5-845932ec7b12', 'Mover', 'V1', '// robot  ''mover''

def damage, dx, dy

// save current damage
start:
    DAMAGE -> damage

// check damage - go move if damaged
// if not, increment aim
scan:
    goto move unless DAMAGE = damage
    AIM + 13 -> AIM

// send radar in direction of aim
// exit to scan if no enemy found
// or shoot at enemy just spotted
// then go see if enemy is still there

spot:
    AIM -> RADAR
    goto scan if RADAR > 0
    0 - RADAR -> SHOT
    goto spot

// pick a random place to go

move:
    50 + RANDOM * 500 -> dx
    50 + RANDOM * 400 -> dy

// travel to new x location
moveX:
    dx - X -> SPEEDX
    goto moveX if abs(dx - X) > 20
    0 -> SPEEDX

// travel to new y location
// then go start scanning again

moveY:
   dy - Y -> SPEEDY
   goto moveY if abs(dy - Y) > 20
   0 -> SPEEDY
   goto start
', 1, 0, '2021-02-16 13:12:56.453469', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('c3bc9059-193a-4f6b-b22b-f2fc64bbb011', 'Battering Ram++', 'V2', '// robot ''battering ram''
// scans for other robots, shoots at found ones and goes for the kill
// ramming them.

state main() {
    def angle = 0
    while true {
        def r = @radar(angle)
        if r < 0 {
            yield charge(angle, abs(r))
        }
        angle = angle + 11
    }
}

state charge(angle, distance) {
    @log(angle)
    @log(distance)
    def dx = distance * cos(angle)
    def dy = distance * sin(angle)
    @speed(dx, dy)
    @fire(angle, distance)
    yield joust(@x() + dx, @y() + dy, angle, distance)
}

state joust(destX, destY, angle, distance) {
    def dx = @x() - destX
    def dy = @y() - destY
    if abs(dx) < 20 {
        @speedX(0)
    }
    if abs(dy) < 20 {
        @speedY(0)
    }
    if @speedX() == 0 and @speedY() == 0 {
        yield main()
    }
    @fire(angle, distance)//sqrt(dx*dx + dy*dy))
}', 1, 0, '2021-02-16 13:12:56.466863', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('4369aafe-e097-46c5-8c11-5d617a035d60', '~shooterbee~', 'V2', 'def angle = 0

state main() {
    def damage = @damage()
    while damage == @damage() {
        def r = @radar(angle)
        if r < 0 {
            @fire(angle, abs(r))
        } else {
            angle = angle + 7
        }
    }
    yield newDest()
}

state newDest() {
    def x = @random(40, 600)
    def y = @random(40, 440)
    @log(x)
    @log(y)
    @speedX(x - @x())
    @speedY(y - @y())
    yield move(x, y)
}

state move(x, y) {
    while true {
        if abs(x - @x()) < 40 {
            @speedX(0)
        }
        if abs(y - @y()) < 40 {
            @speedY(0)
        }
        if @speedX() == 0 and @speedY() == 0 {
            yield main
        }
        def r = @radar(angle)
        if r < 0 {
            @fire(angle, abs(r))
        } else {
            angle = angle + 7
        }
    }
}
', 1, 0, '2021-02-16 13:19:00.256569', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('0e2c9843-7874-4514-848b-457cbc803009', '~stingingbee~', 'V2', 'state main() {
    def destX = @random(40, 600)
    def destY = @random(40, 440)
    @log(destX)
    @log(destY)
    @speed(destX - @x(), destY - @y())
    yield move(destX, destY)
}

def angle

state move(destX, destY) {
    def damage = @damage()
    while true {
        if @damage() != damage {
            yield main
        }
        if abs(destX - @x()) < 40 {
            @speedX(0)
        }
        if abs(destY - @y()) < 40 {
            @speedY(0)
        }
        if @speedX() == 0 and @speedY() == 0 {
            yield main()
        }
        def r = @radar(angle)
        if r < 0 {
            @fire(angle, abs(r))
        }
        angle = angle + 23
    }
}
', 1, 0, '2021-02-16 13:19:00.288329', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('0ae58660-5c94-423c-b3e1-86f8b14be9d7', '~batteringram~', 'V2', 'state main() {
    def angle = 0
    while true {
        def r = @radar(angle)
        if r < 0 {
            yield charge(angle, abs(r))
        }
        angle = angle + 11
    }
}

state charge(angle, distance) {
    @log(angle)
    @log(distance)
    def dx = distance * cos(angle)
    def dy = distance * sin(angle)
    @speed(dx, dy)
    @fire(angle, distance)
    yield joust(@x() + dx, @y() + dy, angle, distance)
}

state joust(destX, destY, angle, distance) {
    def dx = @x() - destX
    def dy = @y() - destY
    if abs(dx) < 20 {
        @speedX(0)
    }
    if abs(dy) < 20 {
        @speedY(0)
    }
    if @speedX() == 0 and @speedY() == 0 {
        yield main()
    }
    @fire(angle, distance)//sqrt(dx*dx + dy*dy))
}', 1, 0, '2021-02-16 13:19:00.305032', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('0d31364d-bd16-45d2-8809-43fabd9e4098', '~circler~', 'V2', 'def angle
def cx = 640/2, cy = 480/2
def radius = cy - 30
def minRadarAngle, maxRadarAngle
def radarAngle

state main() {
    def dx = @x() - cx, dy = @y() - cy
    angle = atan(dy / dx)
    if dx < 0 {
        angle = angle + 180
    }
    yield newDest(true)
}

state newDest(init) {
    def x = cx + cos(angle) * radius
    def y = cy + sin(angle) * radius
    minRadarAngle = angle + 180 - 30
    maxRadarAngle = angle + 180 + 30
    @log(x)
    @log(y)
    radarAngle = minRadarAngle
    angle = angle + 15
    if init {
        yield moveSlowly(x, y)
    } else {
        @speed(2 * (x - @x()), 2 * (y - @y()))
        yield move(x, y)
    }
}

state moveSlowly(x, y) {
    def dx = x - @x(), dy = y - @y()
    @speed(dx, dy)
    if abs(dx) < 20 and abs(dy) < 20 {
        yield newDest(false)
    }
}

state move(x, y) {
    def r = @radar(radarAngle)
    if r < 0 {
        //@fire(radarAngle, abs(r))
    }
    radarAngle = radarAngle + 7
    if radarAngle > maxRadarAngle {
        radarAngle = minRadarAngle
    }
    def dx = x - @x(), dy = y - @y()
    if sqrt(dx*dx + dy*dy) < 10 {
        yield newDest(false)
    }
}
', 1, 0, '2021-02-16 13:19:00.224683', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('25dc5c6c-acb3-43a6-9c20-9e0c6aa76375', '~wanderer~', 'V2', 'state main() {
    yield startMove(30, 610)
}

state startMove(destX, nextDestX) {
    def s = destX - @x()
    if s < 0 {
        s = 0 - 80
    } else {
        s = 80
    }
    @speedX(s)
    yield move(destX, nextDestX)
}

state move(destX, nextDestX) {
    @speedY(sin(@x()) * 50)
    @log(@speedY())
    if abs(@x() - destX) < 10 {
        yield startMove(nextDestX, destX)
    }
}
', 1, 0, '2021-02-16 13:19:00.534250', null);
INSERT INTO robot (id, name, language, code, acceleration, rgb, date_created, date_modified) VALUES ('785cbc2e-43cf-416b-a13e-fc9a1d510639', '~follower~', 'V2', '
state main() {
    def angle
    while true {
        def r = @radar(angle)
        if r < 0 {
            yield follow(angle, abs(r))
        }
        angle = angle + 7
    }
}

state follow(angle, distance) {
    def dx = distance * cos(angle)
    def dy = distance * sin(angle)
    @speed(dx, dy)
    def destX = @x() + dx
    def destY = @y() + dy
    @log(destX)
    @log(destY)
    yield move(destX, destY, angle)
}

state move(destX, destY, angle) {
    def minAngle = angle - 16
    def maxAngle = angle + 16
    def incAngle = 7
    def a = angle
    while true {
        a = a + incAngle
        if a > maxAngle or a < minAngle {
            incAngle = 0 - incAngle
        }
        def r = @radar(a)
        if r < 0 {
            yield follow(a, abs(r))
        }
        def dx = destX - @x()
        def dy = destY - @y()
        if sqrt(dx*dx + dy*dy) < 20 {
            @speed(0, 0)
            yield main()
        }
    }
}
', 1, 0, '2021-02-16 13:19:00.182628', null);