-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: test
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `elo_history`
--

DROP TABLE IF EXISTS `elo_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `elo_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace_id` int NOT NULL,
  `player_id` int NOT NULL,
  `match_id` int NOT NULL,
  `old_elo` int NOT NULL,
  `elo_change` int NOT NULL,
  `new_elo` int NOT NULL,
  `recorded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `workspace_id` (`workspace_id`),
  KEY `player_id` (`player_id`),
  KEY `match_id` (`match_id`),
  CONSTRAINT `fk_elo_match` FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`),
  CONSTRAINT `fk_elo_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`),
  CONSTRAINT `fk_elo_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `match_score_events`
--

DROP TABLE IF EXISTS `match_score_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `match_score_events` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace_id` int NOT NULL,
  `tournament_id` int DEFAULT NULL,
  `match_id` int NOT NULL,
  `team_id` int NOT NULL,
  `player_id` int DEFAULT NULL,
  `rack_number` int NOT NULL,
  `points_received` int NOT NULL,
  `recorded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `note` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `workspace_id` (`workspace_id`),
  KEY `tournament_id` (`tournament_id`),
  KEY `match_id` (`match_id`),
  KEY `team_id` (`team_id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `fk_mse_match` FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mse_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`),
  CONSTRAINT `fk_mse_team` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `fk_mse_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_mse_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace_id` int NOT NULL,
  `tournament_id` int DEFAULT NULL,
  `team1_id` int NOT NULL,
  `team2_id` int NOT NULL,
  `score_team1` int DEFAULT '0',
  `score_team2` int DEFAULT '0',
  `match_type` enum('GROUP','QUARTERFINAL','SEMIFINAL','FINAL','THIRD_PLACE','LAST16','LAST32') DEFAULT 'GROUP',
  `match_category` enum('TOURNAMENT','FUN','BETTING') DEFAULT 'TOURNAMENT',
  `bet_amount` decimal(12,2) DEFAULT NULL,
  `match_date` datetime NOT NULL,
  `note` text,
  `winner_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `workspace_id` (`workspace_id`),
  KEY `tournament_id` (`tournament_id`),
  KEY `team1_id` (`team1_id`),
  KEY `team2_id` (`team2_id`),
  KEY `winner_id` (`winner_id`),
  CONSTRAINT `fk_matches_team1` FOREIGN KEY (`team1_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `fk_matches_team2` FOREIGN KEY (`team2_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `fk_matches_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_matches_winner` FOREIGN KEY (`winner_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `fk_matches_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `players` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace_id` int NOT NULL,
  `name` varchar(150) NOT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `description` text,
  `joined_date` date NOT NULL DEFAULT (curdate()),
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `workspace_id` (`workspace_id`),
  CONSTRAINT `fk_players_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team_players`
--

DROP TABLE IF EXISTS `team_players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_players` (
  `id` int NOT NULL AUTO_INCREMENT,
  `team_id` int NOT NULL,
  `player_id` int NOT NULL,
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_captain` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `fk_team_players_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_players_team` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace_id` int NOT NULL,
  `team_name` varchar(150) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `workspace_id` (`workspace_id`),
  CONSTRAINT `fk_teams_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tournament_players`
--

DROP TABLE IF EXISTS `tournament_players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournament_players` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tournament_id` int NOT NULL,
  `player_id` int NOT NULL,
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `seed_number` int DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `note` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tournament_player` (`tournament_id`,`player_id`),
  KEY `fk_tp_player` (`player_id`),
  CONSTRAINT `fk_tp_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_tp_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tournament_teams`
--

DROP TABLE IF EXISTS `tournament_teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournament_teams` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tournament_id` int NOT NULL,
  `team_id` int NOT NULL,
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `seed_number` int DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `note` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tournament_team` (`tournament_id`,`team_id`),
  KEY `fk_tt_team` (`team_id`),
  CONSTRAINT `fk_tt_team` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_tt_tournament` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tournaments`
--

DROP TABLE IF EXISTS `tournaments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournaments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workspace_id` int NOT NULL,
  `name` varchar(150) NOT NULL,
  `tournament_type` enum('ROUND_ROBIN','SINGLE_ELIMINATION','DOUBLE_ELIMINATION','CUSTOM','ROUND_ROBIN_AND_SINGLE_ELIMINATION','SWEDISH') NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `prize` varchar(100) NOT NULL,
  `winner_id` int DEFAULT NULL,
  `runner_up_id` int DEFAULT NULL,
  `third_place_id` int DEFAULT NULL,
  `description` text,
  `rules` text,
  `banner` varchar(200) DEFAULT NULL,
  `status` enum('ONGOING','UPCOMING','FINISHED','PAUSED') NOT NULL DEFAULT 'UPCOMING',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `workspace_id` (`workspace_id`),
  KEY `winner_id` (`winner_id`),
  KEY `runner_up_id` (`runner_up_id`),
  KEY `third_place_id` (`third_place_id`),
  CONSTRAINT `fk_tournaments_runnerup` FOREIGN KEY (`runner_up_id`) REFERENCES `players` (`id`),
  CONSTRAINT `fk_tournaments_third` FOREIGN KEY (`third_place_id`) REFERENCES `players` (`id`),
  CONSTRAINT `fk_tournaments_winner` FOREIGN KEY (`winner_id`) REFERENCES `players` (`id`),
  CONSTRAINT `fk_tournaments_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workspaces`
--

DROP TABLE IF EXISTS `workspaces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workspaces` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `share_key` int unsigned NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `share_key` (`share_key`),
  CONSTRAINT `workspaces_chk_1` CHECK ((`share_key` between 10000000 and 99999999))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-11 21:57:20
