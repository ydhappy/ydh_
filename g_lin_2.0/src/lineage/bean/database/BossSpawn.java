package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class BossSpawn {
	private String monster;
	private String spawnTime;
	private String spawnDay;
	private List<String> spawnCoords = new ArrayList<>(); // ⬅️ 추가: 좌표 리스트

	public String getMonster() {
		return monster;
	}

	public void setMonster(String monster) {
		this.monster = monster;
	}

	public String getSpawnTime() {
		return spawnTime;
	}

	public void setSpawnTime(String spawnTime) {
		this.spawnTime = spawnTime;
	}

	public String getSpawnDay() {
		return spawnDay;
	}

	public void setSpawnDay(String spawnDay) {
		this.spawnDay = spawnDay;
	}

	public List<String> getSpawnCoords() {
		return spawnCoords;
	}

	public void setSpawnCoords(List<String> spawnCoords) {
		this.spawnCoords = spawnCoords;
	}
}
