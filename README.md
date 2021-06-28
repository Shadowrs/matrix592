# matrix592

Changes to base:
- gradle added
- replaced some but not all usages of 'fastExecutor' with engine-correct calls (see: design flaws section below)- specifically Item Switching and Prayers



[base release by _jordan@rune-server](https://www.rune-server.ee/runescape-development/rs-503-client-server/downloads/674798-matrix-592-tons-features.html)

Notable Features shortlist
- dynamic regions
- varp management
- modern pathfinding
- isaac disabled, RSA enabled
- js5 cache streaming: dll's and HD natives files prepacked (no external/additional CacheDownloader needed for natives)
- some cache dumping tools, a wiki dumper
- cutscenes, construction

[Client download](https://www.dropbox.com/s/zfhp3ftzqmqpxn5/Client592.zip?dl=0) (38mb)
<br>
IMPORTANT: client HD only works when run with Java 6 JRE. Zipped client comes with JRE6 bundled (~110mb packed) and a windows .bat file to run the client using bundled jre6.
<br>
If run with java7+ the dll's will fail to load (jaclib.dll etc) when HD is selected in the graphics options menu

<br>
Notable Dementhium server design flaws from its creation in ~2011-2012
<br>

- CoreManager.fastExecutor calls gamelogic sequentially after the main game cycle, will cause all sorts of side effects: visually, actions happen 1 tick too late<br>
- game logic executed in netty network code (so netty-specific threads)
- Controller / ActivityController system: only one controller can be active at once: forces duplicated code to support area-in-area scenarios. its a bad way of linking an area ingame to code handlers

