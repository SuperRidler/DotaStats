<?php
	$user = "root";
	$pass = "";
	$db = "DotaStats";
	$mysqli = new mysqli("localhost", $user, $pass, $db);
	
	if (isset($_GET["stat"]) && $_GET["stat"] == "gpm" && isset($_GET["id"])) {
		$query = "SELECT * FROM player WHERE account_id = " . $_GET["id"];
		$result = $mysqli->query($query);
		$result->data_seek(0);
		$match = $result->num_rows;
		$gpma =  array();
		while ($row = $result->fetch_assoc()) {
			$gpma[$match--] = $row['gold_per_min']; 
		}
		echo json_encode($gpma);
	} if (isset($_GET["match"])) {
		$query = "SELECT * FROM matches WHERE match_id = " . $_GET["match"];
		$result = $mysqli->query($query);
		$result->data_seek(0);
		$match = array();
		while ($row = $result->fetch_assoc()) {
			$match["start_time"] = $row['start_time'];
			$match["duration"] = $row['duration'];
			$match["tower_status_radiant"] = $row['tower_status_radiant'];
			$match["tower_status_dire"] = $row['tower_status_dire'];
			$match["barracks_status_radiant"] = $row['barracks_status_radiant'];
			$match["barracks_status_dire"] = $row['barracks_status_dire'];
			$match["first_blood_time"] = $row['first_blood_time'];
			$match["lobby_type"] = $row['lobby_type'];
			$match["positive_votes"] = $row['positive_votes'];
			$match["negative_votes"] = $row['negative_votes'];
			$match["game_mode"] = $row['game_mode'];
			$players = array();
			$players[] = $row['player_one'];
			$players[] = $row['player_two'];
			$players[] = $row['player_three'];
			$players[] = $row['player_four'];
			$players[] = $row['player_five'];
			$players[] = $row['player_six'];
			$players[] = $row['player_seven'];
			$players[] = $row['player_eight'];
			$players[] = $row['player_nine'];
			$players[] = $row['player_ten'];
			$match["players"] = array();
			foreach ($players as $player) {
				if ($player != 0) {
					$playerQuery = "SELECT * FROM player WHERE id = " . $player;
					$playerResult = $mysqli->query($playerQuery);
					$playerResult->data_seek(0);
					$playerArray = array();
					while ($playerRow = $playerResult->fetch_assoc()) {
						$playerArray["account_id"] = $playerRow['account_id'];
						$playerArray["player_slot"] = $playerRow['player_slot'];
						$playerArray["hero_id"] = $playerRow['hero_id'];
						$playerArray["item_0"] = $playerRow['item_0'];
						$playerArray["item_1"] = $playerRow['item_1'];
						$playerArray["item_2"] = $playerRow['item_2'];
						$playerArray["item_3"] = $playerRow['item_3'];
						$playerArray["item_4"] = $playerRow['item_4'];
						$playerArray["item_5"] = $playerRow['item_5'];
						$playerArray["kills"] = $playerRow['kills'];
						$playerArray["deaths"] = $playerRow['deaths'];
						$playerArray["assists"] = $playerRow['assists'];
						$playerArray["leaver_status"] = $playerRow['leaver_status'];
						$playerArray["gold"] = $playerRow['gold'];
						$playerArray["last_hits"] = $playerRow['last_hits'];
						$playerArray["denies"] = $playerRow['denies'];
						$playerArray["gold_per_min"] = $playerRow['gold_per_min'];
						$playerArray["xp_per_min"] = $playerRow['xp_per_min'];
						$playerArray["gold_spent"] = $playerRow['gold_spent'];
						$playerArray["hero_damage"] = $playerRow['hero_damage'];
						$playerArray["tower_damage"] = $playerRow['tower_damage'];
						$playerArray["hero_healing"] = $playerRow['hero_healing'];
						$playerArray["level"] = $playerRow['level'];
						$playerArray["ability_upgrades"] = array();
						$abilityQuery = "SELECT * FROM ability_upgrades WHERE id = " . $player;
						$abilityResult = $mysqli->query($abilityQuery);
						$abilityResult->data_seek(0);
						while ($abilityRow = $abilityResult->fetch_assoc()) {
							$ab = array();
							$ab["level"] = $abilityRow['level'];
							$ab["time"] = $abilityRow['time'];
							$ab["ability"] = $abilityRow['ability'];
							$playerArray["ability_upgrades"][] = $ab;
						}
					}
					$match["players"][] = $playerArray;
				}
			}
		}
		echo json_encode($match);
	} else if (isset($_GET["stat"]) && $_GET["stat"] == "matchid" && isset($_GET["id"])) {
		$matchIds = getMatchIdsForPlayer($_GET["id"]);
		echo json_encode($matchIds);
	} else if (isset($_GET["compare"])  && isset($_GET["playerone"]) && isset($_GET["playertwo"])) {
		$account_ids = array();
		$account_ids[] = $_GET["playerone"];
		$account_ids[] = $_GET["playertwo"];
		$jointMatches = getMatchIdsForPlayers($account_ids);
		$playerOneStat = array();
		$playerTwoStat = array();
		foreach ($jointMatches as $match) {
			$query = "SELECT * FROM matches WHERE match_id = " . $match;
			$result = $mysqli->query($query);
			$result->data_seek(0);
			$playerIds = array();
			while ($row = $result->fetch_assoc()) {
				$playerIds[] = $row['player_one'];
				$playerIds[] = $row['player_two'];
				$playerIds[] = $row['player_three'];
				$playerIds[] = $row['player_four'];
				$playerIds[] = $row['player_five'];
				$playerIds[] = $row['player_six'];
				$playerIds[] = $row['player_seven'];
				$playerIds[] = $row['player_eight'];
				$playerIds[] = $row['player_nine'];
				$playerIds[] = $row['player_ten'];
				foreach ($playerIds as $playerId) {
					$playerQuery = "SELECT * FROM player WHERE id = " . $playerId;
					$playerResult = $mysqli->query($playerQuery);
					$playerResult->data_seek(0);
					while ($roww = $playerResult->fetch_assoc()) {
						if ($roww['account_id'] == $_GET["playerone"]) {
							if ($_GET["compare"] == "xpm") {
								$playerOneStat[] = $roww['xp_per_min'];
							} else if ($_GET["compare"] == "gpm") {
								$playerOneStat[] = $roww['gold_per_min'];
							}
						} else if ($roww['account_id'] == $_GET["playertwo"]) {
							if ($_GET["compare"] == "xpm") {
								$playerTwoStat[] = $roww['xp_per_min'];
							} else if ($_GET["compare"] == "gpm") {
								$playerTwoStat[] = $roww['gold_per_min'];
							}
						}
					}
				}
			}
		}
		$compare = array();
		$compare[$_GET["playerone"]] = $playerOneStat;
		$compare[$_GET["playertwo"]] = $playerTwoStat;
		echo json_encode($compare);
	}
	
	/* Get all the match ids for a specific player. */
	function getMatchIdsForPlayer($account_id) {
		global $mysqli;
		$query = "SELECT matches.match_id FROM player INNER JOIN matches ON 
				 (player.id = matches.player_one OR player.id = matches.player_two OR player.id = matches.player_three Or player.id = matches.player_four OR
				  player.id = matches.player_five OR player.id = matches.player_six OR player.id = matches.player_seven Or player.id = matches.player_eight OR
				  player.id = matches.player_nine OR player.id = matches.player_ten) 
				  WHERE player.account_id = " . $account_id;
		$result = $mysqli->query($query);
		$result->data_seek(0);
		$match = $result->num_rows;
		$matchIds =  array();
		while ($row = $result->fetch_assoc()) {
			$matchIds[] = $row['match_id']; 
		}
		return $matchIds;
	}
	
	/* Get the matches that all these players were together. */
	function getMatchIdsForPlayers($account_ids) {
		$matches = array();
		foreach ($account_ids as $account_id) {
			$matches[] = getMatchIdsForPlayer($account_id);
		}
		$result = array_intersect($matches[0], $matches[1]);
		for ($i = 2; $i < count($matches); $i++) {
			$result = array_intersect($result, $matches[$i]);
		}
		return $result;
	}
?>