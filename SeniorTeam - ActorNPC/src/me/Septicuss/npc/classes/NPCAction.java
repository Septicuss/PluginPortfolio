package me.Septicuss.npc.classes;

public enum NPCAction {

	JUMP(1), HIT(2), CROUCH(3), UNCROUCH(4), DESTROY(5);

	private int id;

	NPCAction(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

}
