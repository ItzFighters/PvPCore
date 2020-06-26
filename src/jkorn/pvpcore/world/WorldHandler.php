<?php

declare(strict_types=1);

namespace jkorn\pvpcore\world;

use jkorn\pvpcore\PvPCore;
use jkorn\pvpcore\utils\PvPCKnockback;
use pocketmine\level\Level;
use pocketmine\Player;
use pocketmine\Server;
use pocketmine\utils\Config;

/**
 * Created by PhpStorm.
 * User: jkorn2324
 * Date: 2019-04-05
 * Time: 11:33
 */
class WorldHandler
{

    /** @var PvPCWorld[]|array */
    private $worlds;

    /** @var string */
    private $path;
    /** @var Server */
    private $server;
    /** @var PvPCore */
    private $core;

    /**
     * WorldHandler constructor.
     * @param PvPCore $core
     */
    public function __construct(PvPCore $core)
    {
        $this->core = $core;
        $this->server = $core->getServer();

        $this->worlds = [];
        $this->path = $core->getDataFolder() . "worlds.json";

        $this->initWorlds();
    }

    /**
     * Initialize the worlds.
     */
    private function initWorlds(): void
    {
        if (!file_exists($this->path)) {
            $file = fopen($this->path, "w");
            fclose($file);

            // Decodes the data worlds based on the old format.
            $config = $this->core->getDataFolder() . "/config.yml";
            if(file_exists($config))
            {
                $data = yaml_parse_file($config);
                if(isset($data["worlds"]))
                {
                    $worlds = $data["worlds"];
                    foreach($worlds as $worldName => $data)
                    {
                        $pvpCWorld = PvPCWorld::decodeLegacy($worldName, $data);
                        if($pvpCWorld instanceof PvPCWorld)
                        {
                            $this->worlds[$pvpCWorld->getLocalizedLevel()] = $pvpCWorld;
                        }
                    }
                }
            }
            return;
        }

        $contents = json_decode(file_get_contents($this->path), true);
        foreach ($contents as $worldName => $data) {
            $decoded = PvPCWorld::decode($worldName, $data);
            if ($decoded !== null) {
                $this->worlds[$decoded->getLocalizedLevel()] = $decoded;
            }
        }
    }

    /**
     * @param string|Level $level
     * @return PvPCWorld|null
     *
     * Gets the pvp world from the level (name or instance).
     */
    public function getWorld($level)
    {
        if ($level instanceof Level) {
            if (!isset($this->worlds[$levelName = $level->getName()])) {
                $this->worlds[$levelName] = new PvPCWorld($levelName, true, new PvPCKnockback());
            }
            return $this->worlds[$levelName];
        } elseif (is_string($level)) {
            $loaded = true;
            if (!$this->server->isLevelLoaded($level)) {
                $loaded = $this->server->loadLevel($level);
            }

            if (!$loaded) {
                return null;
            }
            return $this->getWorld($this->server->getLevelByName($level));
        }
        return null;
    }

    /**
     * @param Player $player1
     * @param Player $player2
     * @return PvPCWorld|null
     *
     * Gets the world used for knockback based on the players.
     */
    public function getWorldKnockback(Player $player1, Player $player2): ?PvPCWorld
    {
        foreach($this->worlds as $world)
        {
            if($world->canUseKnocback($player1, $player2))
            {
                return $world;
            }
        }

        return null;
    }

    /**
     * Gets all of the PvPCore worlds.
     * @return PvPCWorld[]
     */
    public function getWorlds()
    {
        $worlds = [];
        $levels = $this->server->getLevels();
        foreach($levels as $level)
        {
            $pvpCWorld = $this->getWorld($level);
            if($pvpCWorld instanceof PvPCWorld)
            {
                $worlds[] = $pvpCWorld;
            }
        }
        return $worlds;
    }

    /**
     * Saves all the worlds data to the file.
     */
    public function save(): void
    {
        // TODO
    }

    /**
     * @param PvPCWorld $world
     *
     * Updates the world to the world handler.
     */
    public function updateWorld(PvPCWorld $world): void
    {
        /* $map = $world->toMap();
        $name = $world->getLevel()->getName();
        $worlds = $this->config->get("worlds");
        if(is_array($worlds) and key_exists($name, $worlds)){
            $worlds[$name] = $map;
            $this->config->set("worlds", $worlds);
            $this->config->save();
        } */
    }
}