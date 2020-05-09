/**
 * This package defines the messages that can be exchanged via the SYNC protocol.
 * The main class is {@link net.smackem.jobotwar.io.sync.messaging.Message}.
 * Here is are samples for the supported message types:
 * <code>
 * {
 *     "init": {
 *         "robots": {
 *             "3187d1e5-f298-464a-bba4-6f68b4efc326": {
 *                 "name": "Robot 2",
 *                 "color": "#d0ff00",
 *                 "x": 100.3,
 *                 "y": 200.6
 *             },
 *             "3187d1e5-f298-464a-bba4-6f68b4efc327": {
 *                 "name": "Robot 3",
 *                 "color": "#d0ff80",
 *                 "x": 150.3,
 *                 "y": 500.6
 *             }
 *         }
 *     },
 *     "game": {
 *          "status": "READY|PLAY|PAUSE"
 *     }
 *     "turn": {
 *         "turnid": 1,
 *         "robots": {
 *             "3187d1e5-f298-464a-bba4-6f68b4efc326": {
 *                  "radar": 123.34,
 *                  "aim": 123.34,
 *                  "random": 4711.42,
 *                  "shot": 1000.0,
 *                  "speedX": 54.3,
 *                  "speedY": 34.2
 *             },
 *             "3187d1e5-f298-464a-bba4-6f68b4efc327": {
 *                  "radar": 123.34,
 *                  "aim": 123.34,
 *                  "random": 4711.42,
 *                  "shot": 1000.0,
 *                  "speedX": 54.3,
 *                  "speedY": 34.2
 *             }
 *         }
 *     },
 * }
 * </code>
 */
package net.smackem.jobotwar.io.sync.messaging;
