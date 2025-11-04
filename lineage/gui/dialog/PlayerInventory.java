package lineage.gui.dialog;

import java.sql.Connection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_SoundEffect;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class PlayerInventory {

	static private Shell shell;
	// 각 스탭마다 변경될 부분
	static private Composite composite_controller;
	// 왼쪽 박스에 표현될 라벨
	static private Label label_step1;
	static private Label label_step2;
	static private Label label_step3;
	// 왼쪽 박스에 표현될 글자 폰트 정보
	static private Font normal;
	static private Font select;
	// 해당 창에 타이틀 명
	static private String title;
	//
	static private Connection con;
	//
	static private PcInstance pc;
	
	static {
		normal = SWTResourceManager.getFont("맑은 고딕", 9, SWT.NORMAL);
		select = SWTResourceManager.getFont("맑은 고딕", 9, SWT.BOLD);
		title = "사용자 인벤토리";
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	static public void open(PcInstance pc) {
		try {
			con = DatabaseConnection.getLineage();
		} catch (Exception e) { }
		
		PlayerInventory.pc = pc;
		
		shell = new Shell(GuiMain.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		shell.setSize(640, 480);
		shell.setText(title);
		
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.horizontalSpacing = 2;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);
		
		Composite composite_status = new Composite(shell, SWT.NONE);
		composite_status.setLayout(new GridLayout(1, false));
		composite_status.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		label_step1 = new Label(composite_status, SWT.NONE);
		label_step1.setText("아이템 선별");
		
		label_step2 = new Label(composite_status, SWT.NONE);
		label_step2.setText("정보 수정");
		
		label_step3 = new Label(composite_status, SWT.NONE);
		label_step3.setText("완료");
		
		composite_controller = new Composite(shell, SWT.NONE);
		
		step1();
//		step2(null, null);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!GuiMain.display.readAndDispatch()) 
				GuiMain.display.sleep();
		}
		
		composite_controller.dispose();
		label_step3.dispose();
		label_step2.dispose();
		label_step1.dispose();
		composite_status.dispose();
		
		DatabaseConnection.close(con);
	}

	static private void step1() {
	    // 이전 내용들 다 제거.
	    for (Control c : composite_controller.getChildren())
	        c.dispose();

	    selectStep(1);
	    if (checkBug())
	        return;

	    GridLayout gl_composite_controller = new GridLayout(3, false); // 열 수를 5로 설정
	    gl_composite_controller.verticalSpacing = 0;
	    gl_composite_controller.horizontalSpacing = 2;
	    composite_controller.setLayout(gl_composite_controller);
	    composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	    Composite composite_1 = new Composite(composite_controller, SWT.NONE);
	    GridLayout gl_composite_1 = new GridLayout(2, false);
	    composite_1.setLayout(gl_composite_1);
	    composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

	    final Text text = new Text(composite_1, SWT.BORDER);
	    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	    Button button_4 = new Button(composite_1, SWT.NONE);
	    button_4.setText("검색");

	    Group group_1 = new Group(composite_controller, SWT.NONE);
	    group_1.setText("아이템");
	    group_1.setLayout(new GridLayout(1, false));
	    group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

	    // 아이템 테이블 초기화
	    Table table = new Table(group_1, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
	    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    // 아이템 코드 열 크기
	    TableColumn columnCode = new TableColumn(table, SWT.LEFT);
	    columnCode.setText("아이템 코드");
	    columnCode.setWidth(80); // 아이템 코드 칸 크기

	    // 아이템 이름 열 크기 조정
	    TableColumn columnName = new TableColumn(table, SWT.LEFT);
	    columnName.setText("아이템 이름");
	    columnName.setWidth(150); // 아이템 이름 칸 크기 조정
		new Label(composite_controller, SWT.NONE);
		
	    // 인벤토리 그룹
	    Group group = new Group(composite_controller, SWT.NONE);
	    group.setText("인벤토리");
	    group.setLayout(new GridLayout(1, false));
	    group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

	    // 인벤토리 테이블 초기화
	    Table inventoryTable = new Table(group, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
	    inventoryTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    inventoryTable.setHeaderVisible(true);
	    inventoryTable.setLinesVisible(true);

	    // 인벤토리 코드 열 크기
	    TableColumn columnInvCode = new TableColumn(inventoryTable, SWT.LEFT);
	    columnInvCode.setText("아이템 코드");
	    columnInvCode.setWidth(80); // 인벤토리 코드 칸 크기

	    // 인벤토리 이름 열 크기 조정
	    TableColumn columnInvName = new TableColumn(inventoryTable, SWT.LEFT);
	    columnInvName.setText("아이템 이름");
	    columnInvName.setWidth(150); // 인벤토리 이름 칸 크기 조정

		Button button_1 = new Button(composite_controller, SWT.NONE);
		button_1.setToolTipText("추가");
		button_1.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1));
		button_1.setText("->");
		
		Button button_2 = new Button(composite_controller, SWT.NONE);
		button_2.setToolTipText("제거");
		button_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		button_2.setText("<-");
		new Label(composite_controller, SWT.NONE);
		new Label(composite_controller, SWT.NONE);
		
		Button button = new Button(composite_controller, SWT.NONE);
		GridData gd_button = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("다음");

	    // 검색 이벤트
	    text.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyReleased(KeyEvent e) {
	            if (e.keyCode == 13 || e.keyCode == 16777296) {
	                toSearchItem(text, table);
	            }
	        }
	    });

	    button_4.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            toSearchItem(text, table);
	        }
	    });

	    button_1.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            if (table.getSelectionCount() <= 0)
	                return;

	            // 추가
	            for (TableItem selectedItem : table.getSelection()) {
	                TableItem newItem = new TableItem(inventoryTable, SWT.NONE);
	                newItem.setText(new String[]{selectedItem.getText(0), selectedItem.getText(1)}); // 아이템 코드와 이름 추가
	            }
	            inventoryTable.setTopIndex(inventoryTable.getItemCount() - 1);
	        }
	    });

	    button_2.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            if (inventoryTable.getSelectionCount() <= 0)
	                return;
	            int select = inventoryTable.getSelectionIndex();
	            inventoryTable.remove(select);
	        }
	    });

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (inventoryTable.getItemCount() == 0) {
					GuiMain.toMessageBox("인벤토리 목록에 아이템을 추가하여 주십시오.");
					return;
				}

				// TableItem[] -> String[] 변환 (코드와 이름을 번갈아 저장)
				TableItem[] items = inventoryTable.getItems();
				String[] inv_list = new String[items.length * 2]; // 2배 크기로 생성 (코드 + 이름 저장)

				for (int i = 0; i < items.length; i++) {
				    inv_list[i * 2] = items[i].getText(0);   // 0번 열: 아이템 코드
				    inv_list[i * 2 + 1] = items[i].getText(1); // 1번 열: 아이템 이름
				}

				// Map<Integer, Object> 생성 (역순으로 저장)
				Map<Integer, Object> list = new HashMap<>();
				for (int i = items.length - 1; i >= 0; --i) {
					list.put(i, inventoryTable.getData(String.valueOf(i))); // 저장된 ItemInstance 가져오기
				}

				// step2 호출 (String[] 형식 맞춤)
				step2(inv_list, list);
			}
		});

		// 정보 갱신
		for (Item i : ItemDatabase.getList()) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(new String[] { String.valueOf(i.getItemCode()), i.getName() });
		}

		// 인벤토리 초기화 (ItemInstance 저장 추가)
		for (ItemInstance ii : pc.getInventory().getList()) {
			TableItem invTableItem = new TableItem(inventoryTable, SWT.NONE);
			invTableItem.setText(new String[] { String.valueOf(ii.getItem().getItemCode()), ii.getItem().getName() });
			inventoryTable.setData(String.valueOf(inventoryTable.getItemCount() - 1), ii); // ItemInstance 저장
		}

		composite_controller.layout();
	}

	/**
	 * 아이템 검색
	 * @param text
	 * @param table
	 */
	static private void toSearchItem(Text text, Table table) {
	    String name = text.getText().toLowerCase();

	    // 이전 기록 제거
	    table.removeAll();

	    // 검색명이 없을 경우 전체 표현.
	    if (name == null || name.length() <= 0) {
	        if (ItemDatabase.getList().size() > 0) {
	            for (Item i : ItemDatabase.getList()) {
	                TableItem tableItem = new TableItem(table, SWT.NONE);
	                tableItem.setText(new String[] { String.valueOf(i.getItemCode()), i.getName() });
	            }
	        } else {
	            GuiMain.toMessageBox("아이템이 존재하지 않습니다.");
	        }
	        return;
	    }

	    // 검색.
	    for (Item i : ItemDatabase.getList()) {
	        int pos = i.getName().toLowerCase().indexOf(name);
	        if (pos >= 0) {
	            TableItem tableItem = new TableItem(table, SWT.NONE);
	            tableItem.setText(new String[] { String.valueOf(i.getItemCode()), i.getName() });
	        }
	    }

	    // 등록된 게 없을 경우 안내 멘트.
	    if (table.getItemCount() <= 0)
	        GuiMain.toMessageBox("일치하는 아이템이 없습니다.");

	    // 포커스.
	    text.setFocus();
	}
	
	static private void step2(String[] inv_list, Map<Integer, Object> list){
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(2);
		if(checkBug())
			return;
		
	    GridLayout gl_composite_controller = new GridLayout(2, false);
	    gl_composite_controller.verticalSpacing = 0;
	    gl_composite_controller.horizontalSpacing = 2;
	    composite_controller.setLayout(gl_composite_controller);
	    composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    
	    final Table table = new Table(composite_controller, SWT.FULL_SELECTION);
	    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    final TableEditor editor = new TableEditor(table);
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
		
		TableColumn tblclmnUid = new TableColumn(table, SWT.NONE);
		tblclmnUid.setWidth(40);
		tblclmnUid.setText("objId");
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(65);
		tblclmnName.setText("cha_objId");
		
		TableColumn tblclmnItemname = new TableColumn(table, SWT.NONE);
		tblclmnItemname.setWidth(70);
		tblclmnItemname.setText("cha_name");
		
		TableColumn tblclmnItemcode = new TableColumn(table, SWT.NONE);
		tblclmnItemcode.setWidth(75);
		tblclmnItemcode.setText("item_code");
		
		TableColumn tblclmnItemcount = new TableColumn(table, SWT.NONE);
		tblclmnItemcount.setWidth(130);
		tblclmnItemcount.setText("name");
		
		TableColumn tblclmnItembress = new TableColumn(table, SWT.NONE);
		tblclmnItembress.setWidth(50);
		tblclmnItembress.setText("count");
		
		TableColumn tblclmnItemenlevel = new TableColumn(table, SWT.NONE);
		tblclmnItemenlevel.setWidth(60);
		tblclmnItemenlevel.setText("quantity");
		
		TableColumn tblclmnSell = new TableColumn(table, SWT.NONE);
		tblclmnSell.setWidth(50);
		tblclmnSell.setText("en");
		
		TableColumn tblclmnBuy = new TableColumn(table, SWT.NONE);
		tblclmnBuy.setWidth(65);
		tblclmnBuy.setText("equipped");
		
		TableColumn tblclmnGamble = new TableColumn(table, SWT.NONE);
		tblclmnGamble.setWidth(55);
		tblclmnGamble.setText("definite");
		
		TableColumn tblclmnPrice = new TableColumn(table, SWT.NONE);
		tblclmnPrice.setWidth(40);
		tblclmnPrice.setText("bless");
		
		TableColumn tblclmnPrice1 = new TableColumn(table, SWT.NONE);
		tblclmnPrice1.setWidth(65);
		tblclmnPrice1.setText("durability");
		
		TableColumn tblclmnPrice2 = new TableColumn(table, SWT.NONE);
		tblclmnPrice2.setWidth(60);
		tblclmnPrice2.setText("nowtime");
		
		TableColumn tblclmnPrice3 = new TableColumn(table, SWT.NONE);
		tblclmnPrice3.setWidth(65);
		tblclmnPrice3.setText("pet_objid");
		
		TableColumn tblclmnPrice4 = new TableColumn(table, SWT.NONE);
		tblclmnPrice4.setWidth(60);
		tblclmnPrice4.setText("inn_key");
		
		TableColumn tblclmnPrice5 = new TableColumn(table, SWT.NONE);
		tblclmnPrice5.setWidth(65);
		tblclmnPrice5.setText("letter_uid");
		
		TableColumn tblclmnPrice6 = new TableColumn(table, SWT.NONE);
		tblclmnPrice6.setWidth(65);
		tblclmnPrice6.setText("slimerace");
		
		Button button_3 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_3 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_button_3.widthHint = 100;
		button_3.setLayoutData(gd_button_3);
		button_3.setText("이전");
		
		Button button_5 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button_5.widthHint = 100;
		button_5.setLayoutData(gd_button_5);
		button_5.setText("다음");
		
		// 이벤트 등록.
		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 4; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								@Override
								public void handleEvent(final Event e) {
									switch (e.type) {
										case SWT.FocusOut:
											item.setText(column, text.getText());
											text.dispose();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													item.setText(column, text.getText());
												case SWT.TRAVERSE_ESCAPE:
													text.dispose();
													e.doit = false;
											}
											break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 이전
				step1();
			}
		});
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				step3(table);
			}
		});
		// 정보 갱신
		int idx = 0;
		for (int i = 0; i < inv_list.length; i += 2) { // 2씩 증가 (코드, 이름 쌍을 올바르게 읽음)
		    String code = inv_list[i];   // 0번 데이터 (아이템 코드)
		    String name = (i + 1 < inv_list.length) ? inv_list[i + 1] : ""; // 1번 데이터 (아이템 이름)

		    Object o = list.get(idx++);
		    ItemInstance ii = o == null ? null : (ItemInstance) o;
		    String[] field = new String[17];

		    field[0] = String.valueOf(ii == null ? 0 : ii.getObjectId());
		    field[1] = String.valueOf(pc.getObjectId());
		    field[2] = pc.getName();
		    field[3] = String.valueOf(ii == null ? code : ii.getItem().getItemCode());			
		    field[4] = name;			
		    field[5] = String.valueOf(ii == null ? 1 : ii.getCount());
		    field[6] = String.valueOf(ii == null ? 0 : ii.getQuantity());
		    field[7] = String.valueOf(ii == null ? 0 : ii.getEnLevel());
		    field[8] = String.valueOf(ii == null ? false : ii.isEquipped());
		    field[9] = String.valueOf(ii == null ? true : ii.isDefinite());
		    field[10] = String.valueOf(ii == null ? 1 : ii.getBless());
		    field[11] = String.valueOf(ii == null ? 0 : ii.getDurability());
		    field[12] = String.valueOf(ii == null ? 0 : ii.getTime());
		    field[13] = String.valueOf(ii == null ? 0 : ii.getPetObjectId());
		    field[14] = String.valueOf(ii == null ? 0 : ii.getInnRoomKey());
		    field[15] = String.valueOf(ii == null ? 0 : ii.getLetterUid());
		    field[16] = String.valueOf(ii == null ? "" : ii.getRaceTicket());

		    new TableItem(table, SWT.NONE).setText(field);
		}
		composite_controller.layout();
	}
	
	static private void step3(Table table) {
		selectStep(3);
		if (checkBug())
			return;

		// 삭제된 아이템 추출
		java.util.List<ItemInstance> list_remove = new ArrayList<ItemInstance>();
		for (ItemInstance ii : pc.getInventory().getList()) {
			ItemInstance find_ii = null;
			// 처리목록에서 둘러보기.
			for (TableItem ti : table.getItems()) {
				if (ii.getObjectId() == Integer.valueOf(ti.getText(0))) {
					find_ii = ii;
					break;
				}
			}
			// 못찾았다면 현재 아이템 제거목록에 등록.
			if (find_ii == null)
				list_remove.add(ii);
		}
		// 아이템 삭제 처리.
		for (ItemInstance ii : list_remove) {
			// 안내 멘트.
			ChattingController.toChatting(pc, String.format("운영자에게 '%s' 반납 하였습니다.", ii.getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
			// 착용중이라면 해제.
			if (ii.isEquipped())
				ii.toClick(pc, null);
			// 삭제 처리.
			pc.getInventory().count(ii, 0, true);
		}
		// 정보 수정 및 새로운 아이템 추가.
		for (TableItem ti : table.getItems()) {
			try {
				int item_objectid = Integer.valueOf(ti.getText(0));
				int item_code = Integer.parseInt(ti.getText(3));
				// String name = ti.getText(4);
				int count = Integer.valueOf(ti.getText(5));
				int quantity = Integer.valueOf(ti.getText(6));
				int en = Integer.valueOf(ti.getText(7));
				// boolean equipped = Boolean.valueOf(ti.getText(8));
				boolean definite = Boolean.valueOf(ti.getText(9));
				int bress = Integer.valueOf(ti.getText(10));
				int durability = Integer.valueOf(ti.getText(11));
				int nowtime = Integer.valueOf(ti.getText(12));
				int pet_objid = Integer.valueOf(ti.getText(13));
				int inn_key = Integer.valueOf(ti.getText(14));
				int letter_uid = Integer.valueOf(ti.getText(15));
				String race = ti.getText(16);

				ItemInstance ii = item_objectid > 0 ? pc.getInventory().value(item_objectid) : ItemDatabase.newInstance(ItemDatabase.find_ItemCode(item_code));
				if (ii != null) {
					ii.setObjectId(item_objectid>0 ? item_objectid : ServerDatabase.nextItemObjId());
					ii.setCount(count);
					ii.setQuantity(quantity);
					ii.setEnLevel(en);
					// ii.setEquipped(equipped);
					ii.setDefinite(definite);
					ii.setBless(bress);
					ii.setDurability(durability);
					ii.setTime(nowtime);
					ii.setPetObjectId(pet_objid);
					ii.setInnRoomKey(inn_key);
					ii.setLetterUid(letter_uid);
					ii.setRaceTicket(race);
					if (item_objectid > 0) {
						//
						pc.getInventory().count(ii, ii.getCount(), true);
					} else {
						//
						pc.getInventory().append(ii, true);
						
						applyItemDuration(pc, ii);
						
						// \f1%0%s 당신에게 %1%o 주었습니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, ServerDatabase.getName(), ii.toString()));
						pc.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 18001));
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("[ERROR] 아이템 데이터를 변환하는 중 오류 발생: " + e.getMessage());
			}
		}

		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();

		GridLayout gl_composite_controller = new GridLayout(1, false);
		gl_composite_controller.verticalSpacing = 0;
		gl_composite_controller.horizontalSpacing = 2;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		List list_2 = new List(composite_controller, SWT.BORDER | SWT.V_SCROLL);
		list_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Button button_6 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_6 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_6.widthHint = 100;
		button_6.setLayoutData(gd_button_6);
		button_6.setText("완료");

		// 이벤트 등록.
		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				shell.dispose();
			}
		});

		// 처리 2.
		list_2.add("메모리 갱신 완료.");
		
		composite_controller.layout();
	}
	
	/**
	 * 스탭에 맞춰서 왼쪽 글씨 폰트 변경하기.
	 * @param step
	 */
	static private void selectStep(int step){
		label_step1.setForeground(step==1 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		label_step2.setForeground(step==2 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		label_step3.setForeground(step==3 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		
		label_step1.setFont(step==1 ? select : normal);
		label_step2.setFont(step==2 ? select : normal);
		label_step3.setFont(step==3 ? select : normal);
	}
	
	static private boolean checkBug(){
		// 버그 확인.
		if(pc.isWorldDelete()){
			GuiMain.toMessageBox("사용자가 월드에 존재하지 않습니다.");
			shell.dispose();
			return true;
		}
		return false;
	}
	
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 *  기간제 아이템 설정 메서드
	 *  (아이템 이름에 '1일','3일','7일','30일','마법인형' 등이 포함되면 +n일)
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	private static void applyItemDuration(PcInstance pc, ItemInstance ii) {
		if (ii == null || ii.getItem() == null) return;

		// 아이템 이름
		String itemName = ii.getItem().getName();
		int daysToAdd = 0;

		if (itemName.contains("1일")) {
			daysToAdd = 1;
		} else if (itemName.contains("3일")) {
			daysToAdd = 3;
		} else if (itemName.contains("7일")) {
			daysToAdd = 7;
		} else if (itemName.contains("30일")) {
			daysToAdd = 30;
		}

		if (daysToAdd > 0) {
			ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
			ZonedDateTime futureKST = nowKST.plusDays(daysToAdd);

			long epochMillis = futureKST.toInstant().toEpochMilli();
			ii.setItemTimek(Long.toString(epochMillis));

			if (pc != null) {
				DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
				String dateString = futureKST.format(fmt);
				String message = String.format("%s 아이템은 %s까지 사용 가능합니다.", ii.getItem().getName(), dateString);

				// 안내
				ChattingController.toChatting(pc, message, Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
