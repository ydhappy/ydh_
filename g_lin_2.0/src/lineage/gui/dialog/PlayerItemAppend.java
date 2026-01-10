package lineage.gui.dialog;

import java.sql.Connection;
import java.util.ArrayList;

import lineage.bean.database.Item;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.world.World;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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

public class PlayerItemAppend {
	
	static private Shell shell;
	// 각 스탭마다 변경될 부분
	static private Composite composite_controller;
	// 왼쪽 박스에 표현될 라벨
	static private Label label_step1;
	static private Label label_step2;
	static private Label label_step3;
	static private Label label_step4;
	// 왼쪽 박스에 표현될 글자 폰트 정보
	static private Font normal;
	static private Font select;
	// 해당 창에 타이틀 명
	static private String title;
	//
	static private Text text_temp = null;
	static private List list_temp = null;
	static private List list_1_temp = null;
	static private Button button_2_temp = null;
	static private Button btnNewButton_temp = null;
	static private Button btnNewButton_1_temp = null;
	// 디비에 등록된 사용자 이름 정보 추출후 담을 변수.
	static private java.util.List<String> list_search_name;
	private static DragSource dragSource;
	private static DropTarget dropTarget;
	
	static {
		normal = SWTResourceManager.getFont("맑은 고딕", 9, SWT.NORMAL);
		select = SWTResourceManager.getFont("맑은 고딕", 9, SWT.BOLD);
		title = "아이템 지급";
		list_search_name = new ArrayList<String>();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 * @wbp.parser.entryPoint
	 */
	static public void open() {
		shell = new Shell(GuiMain.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		shell.setSize(653, 487);
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
		label_step1.setText("사용자 선택");
		
		label_step2 = new Label(composite_status, SWT.NONE);
		label_step2.setText("아이템 선택");
		
		label_step3 = new Label(composite_status, SWT.NONE);
		label_step3.setText("아이템 정보 변경");
		
		label_step4 = new Label(composite_status, SWT.NONE);
		label_step4.setText("완료");
		
		composite_controller = new Composite(shell, SWT.NONE);
		
		step1();
//		step2("전체", new String[]{"1111", "2222"});
//		step3("전체", new String[]{"1111", "2222"}, new String[]{"단검", "일본도"});
//		step4(title, title, map, map, map, map, map, title, true);
		
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
	}

	static private void step1() {
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(1);
		
		composite_controller.setLayout(new GridLayout(1, false));
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite = new Composite(composite_controller, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		final Button btnRadioButton = new Button(composite, SWT.RADIO);
		btnRadioButton.setSelection(true);
		btnRadioButton.setText("선택된 사용자");
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!btnRadioButton.getSelection())
					return;
				text_temp.setEnabled(true);
				list_temp.setEnabled(true);
				list_1_temp.setEnabled(true);
				button_2_temp.setEnabled(true);
				btnNewButton_temp.setEnabled(true);
				btnNewButton_1_temp.setEnabled(true);
			}
		});
		
		final Button button = new Button(composite, SWT.RADIO);
		button.setText("접속된 사용자");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!button.getSelection())
					return;
				text_temp.setEnabled(false);
				list_temp.setEnabled(false);
				list_1_temp.setEnabled(false);
				button_2_temp.setEnabled(false);
				btnNewButton_temp.setEnabled(false);
				btnNewButton_1_temp.setEnabled(false);
			}
		});
		
		final Button button_1 = new Button(composite, SWT.RADIO);
		button_1.setText("전체 사용자");
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!button_1.getSelection())
					return;
				text_temp.setEnabled(false);
				list_temp.setEnabled(false);
				list_1_temp.setEnabled(false);
				button_2_temp.setEnabled(false);
				btnNewButton_temp.setEnabled(false);
				btnNewButton_1_temp.setEnabled(false);
			}
		});
		
		Composite composite_1 = new Composite(composite_controller, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		final Text text = new Text(composite_1, SWT.BORDER);
		text_temp = text;
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_2 = new Button(composite_1, SWT.NONE);
		button_2_temp = button_2;
		button_2.setText("검색");
		
		Composite composite_2 = new Composite(composite_controller, SWT.NONE);
		composite_2.setLayout(new GridLayout(3, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Group group = new Group(composite_2, SWT.NONE);
		group.setText("검색된 사용자");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		
		final List list = new List(group, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list_temp = list;
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		dragSource = new DragSource(list, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton_temp = btnNewButton;
		btnNewButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1));
		btnNewButton.setText(">");
		
		Group group_1 = new Group(composite_2, SWT.NONE);
		group_1.setText("선택된 사용자");
		group_1.setLayout(new GridLayout(1, false));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		
		final List list_1 = new List(group_1, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list_1_temp = list_1;
		list_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		dropTarget = new DropTarget(list_1, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		
		Button btnNewButton_1 = new Button(composite_2, SWT.NONE);
		btnNewButton_1_temp = btnNewButton_1;
		btnNewButton_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		btnNewButton_1.setText("<");
		
		Button button_3 = new Button(composite_controller, SWT.NONE);
		GridData gd_button_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_3.widthHint = 100;
		button_3.setLayoutData(gd_button_3);
		button_3.setText("다음");
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnRadioButton.getSelection() && list_1.getItemCount() <= 0) {
					GuiMain.toMessageBox("아이템을 지급할 사용자를 선택하여 주십시오.");
					return;
				}
				String type = "검색";
				if(button.getSelection())
					type = "접속";
				if(button_1.getSelection())
					type = "전체";
				step2(type, list_1.getItems());
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==13 || e.keyCode==16777296)
					toSearchPlayer(text, list);
			}
		});
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toSearchPlayer(text, list);
			}
		});
		dragSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = list.getSelection()[0];
			}
		});
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				if(event.data instanceof String) {
					boolean isAppend = true;
					String name = (String)event.data;
					for(String val : list_1.getItems()) {
						if(val.equalsIgnoreCase(name)) {
							isAppend = false;
							break;
						}
					}
					if(isAppend)
						list_1.add(name);
					list_1.setTopIndex(list_1.getVerticalBar().getMaximum());
				}
			}
		});
		list_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(list_1.getSelectionCount() <= 0)
					return;
				// 삭제
				if(e.keyCode == SWT.DEL) {
					int select = list_1.getSelectionIndex();
					list_1.setData(String.valueOf(select), null);
					list_1.remove(select);
					
					// 갱신.
					for(int i=select ; i<list_1.getItemCount() ; ++i) {
						// 앞에 이름 추출.
						Object o = list_1.getData( String.valueOf(i+1) );
						list_1.setData(String.valueOf(i), o);
					}
				}
			}
		});
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list.getSelectionCount() <= 0)
					return;
				// 추가
				for(String name : list.getSelection()) {
					boolean isAppend = true;
					for(String val : list_1.getItems()) {
						if(val.equalsIgnoreCase(name)) {
							isAppend = false;
							break;
						}
					}
					if(isAppend)
						list_1.add( name );
				}
				list_1.setTopIndex(list_1.getVerticalBar().getMaximum());
			}
		});
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list_1.getSelectionCount() <= 0)
					return;
				// 삭제
				int select = list_1.getSelectionIndex();
				list_1.setData(String.valueOf(select), null);
				list_1.remove(select);
				
				// 갱신.
				for(int i=select ; i<list_1.getItemCount() ; ++i) {
					// 앞에 이름 추출.
					Object o = list_1.getData( String.valueOf(i+1) );
					list_1.setData(String.valueOf(i), o);
				}
			}
		});
		
		composite_controller.layout();
		
		toSearchPlayer(text, list);
	}

	static private void step2(final String type, final String[] char_list) {
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();

		selectStep(2);
		
		GridLayout gl_composite_controller = new GridLayout(3, false);
		gl_composite_controller.verticalSpacing = 0;
		gl_composite_controller.horizontalSpacing = 2;
		composite_controller.setLayout(gl_composite_controller);
		composite_controller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_1 = new Composite(composite_controller, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.horizontalSpacing = 2;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		final Text text = new Text(composite_1, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_4 = new Button(composite_1, SWT.NONE);
		button_4.setText("검색");
		
		Group group_1 = new Group(composite_controller, SWT.NONE);
		group_1.setText("아이템");
		GridLayout gl_group_1 = new GridLayout(1, false);
		gl_group_1.verticalSpacing = 0;
		gl_group_1.horizontalSpacing = 0;
		gl_group_1.marginHeight = 0;
		gl_group_1.marginWidth = 0;
		group_1.setLayout(gl_group_1);
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		final List list = new List(group_1, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_list.widthHint = 100;
		list.setLayoutData(gd_list);
		
		DragSource dragSource = new DragSource(list, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		new Label(composite_controller, SWT.NONE);
		
		Group group = new Group(composite_controller, SWT.NONE);
		group.setText("인벤토리");
		GridLayout gl_group = new GridLayout(1, false);
		gl_group.verticalSpacing = 0;
		gl_group.horizontalSpacing = 0;
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		final List list_1 = new List(group, SWT.BORDER | SWT.V_SCROLL);
		list_1.setData("down", false);
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_list_1.widthHint = 100;
		list_1.setLayoutData(gd_list_1);
		
		DropTarget dropTarget = new DropTarget(list_1, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });

		
		Button button_1 = new Button(composite_controller, SWT.NONE);
		button_1.setToolTipText("추가");
		button_1.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1));
		button_1.setText("->");
		
		Button button_2 = new Button(composite_controller, SWT.NONE);
		button_2.setToolTipText("제거");
		button_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		button_2.setText("<-");
		
		Composite composite = new Composite(composite_controller, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.horizontalSpacing = 2;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		Button btnNewButton_2 = new Button(composite, SWT.NONE);
		GridData gd_btnNewButton_2 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnNewButton_2.widthHint = 100;
		btnNewButton_2.setLayoutData(gd_btnNewButton_2);
		btnNewButton_2.setText("이전");
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//
				step1();
			}
		});
		
		Button button = new Button(composite, SWT.NONE);
		GridData gd_button = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_button.widthHint = 100;
		button.setLayoutData(gd_button);
		button.setText("다음");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list_1.getItemCount() == 0){
					GuiMain.toMessageBox("인벤토리목록에 아이템을 추가하여 주십시오.");
					return;
				}
				step3(type, char_list, list_1.getItems());
			}
		});
		
		// 이벤트 등록.
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==13 || e.keyCode==16777296)
					// 검색
					toSearchItem(text, list);
			}
		});
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 검색
				toSearchItem(text, list);
			}
		});
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				list_1.setData("down", true);
				list_1.setData("select", list_1.getSelectionIndex());
			}
			@Override
			public void mouseUp(MouseEvent e) {
				list_1.setData("down", false);
			}
		});
		list_1.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				Boolean drag = (Boolean)list_1.getData("down");
				if(drag){
					int select = (Integer)list_1.getData("select");
					int move_idx = list_1.getSelectionIndex();
					if(select != move_idx){
						// 위치 바꾸기.
						String temp = list_1.getItem(select);
						Object temp_o = list_1.getData( String.valueOf(select) );
						list_1.setItem(select, list_1.getItem(move_idx));
						list_1.setData(String.valueOf(select), list_1.getData(String.valueOf(move_idx)));
						list_1.setItem(move_idx, temp);
						list_1.setData(String.valueOf(move_idx), temp_o);
						// 정보 변경.
						list_1.setData("select", move_idx);
						list_1.select(move_idx);
					}
				}
			}
		});
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list.getSelectionCount() <= 0)
					return;
				// 추가
				for(String name : list.getSelection())
					list_1.add( name );
				list_1.setTopIndex(list_1.getVerticalBar().getMaximum());
			}
		});
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list_1.getSelectionCount() <= 0)
					return;
				// 삭제
				int select = list_1.getSelectionIndex();
				list_1.setData(String.valueOf(select), null);
				list_1.remove(select);
				
				// 갱신.
				for(int i=select ; i<list_1.getItemCount() ; ++i) {
					// 앞에 이름 추출.
					Object o = list_1.getData( String.valueOf(i+1) );
					list_1.setData(String.valueOf(i), o);
				}
			}
		});
		list_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(list_1.getSelectionCount() <= 0)
					return;
				// 삭제
				if(e.keyCode == SWT.DEL) {
					int select = list_1.getSelectionIndex();
					list_1.setData(String.valueOf(select), null);
					list_1.remove(select);
					
					// 갱신.
					for(int i=select ; i<list_1.getItemCount() ; ++i) {
						// 앞에 이름 추출.
						Object o = list_1.getData( String.valueOf(i+1) );
						list_1.setData(String.valueOf(i), o);
					}
				}
			}
		});
		dragSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = list.getSelection()[0];
			}
		});
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				if(event.data instanceof String){
					list_1.add( (String)event.data );
					list_1.setTopIndex(list_1.getVerticalBar().getMaximum());
				}
			}
		});
		
		// 정보 갱신
		for(Item i : ItemDatabase.getList())
			list.add(i.getName());
		
		composite_controller.layout();
	}
	
	static private void step3(final String type, final String[] char_list, final String[] item_list) {
		// 이전 내용들 다 제거.
		for(Control c : composite_controller.getChildren())
			c.dispose();
		
		selectStep(3);
		
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

		TableColumn tblclmnItemcount = new TableColumn(table, SWT.NONE);
		tblclmnItemcount.setWidth(50);
		tblclmnItemcount.setText("name");
		
		TableColumn tblclmnItembress = new TableColumn(table, SWT.NONE);
		tblclmnItembress.setWidth(50);
		tblclmnItembress.setText("count");
		
		TableColumn tblclmnItemenlevel = new TableColumn(table, SWT.NONE);
		tblclmnItemenlevel.setWidth(60);
		tblclmnItemenlevel.setText("quantity");
		
		TableColumn tblclmnSell = new TableColumn(table, SWT.NONE);
		tblclmnSell.setWidth(30);
		tblclmnSell.setText("en");
		
		TableColumn tblclmnBuy = new TableColumn(table, SWT.NONE);
		tblclmnBuy.setWidth(65);
		tblclmnBuy.setText("equipped");
		
		TableColumn tblclmnGamble = new TableColumn(table, SWT.NONE);
		tblclmnGamble.setWidth(55);
		tblclmnGamble.setText("definite");
		
		TableColumn tblclmnPrice = new TableColumn(table, SWT.NONE);
		tblclmnPrice.setWidth(40);
		tblclmnPrice.setText("bress");
		
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
					for (int i = 2; i < table.getColumnCount(); i++) {
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
				step2(type, char_list);
			}
		});
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 다음
				step4(type, char_list, table);
			}
		});
		// 정보 갱신
		for(String s : item_list) {
			String[] field = new String[14];
			
			field[0] = "0";
			field[1] = s;
			field[2] = "1";
			field[3] = "0";
			field[4] = "0";
			field[5] = "false";
			field[6] = "false";
			field[7] = "1";
			field[8] = "0";
			field[9] = "0";
			field[10] = "0";
			field[11] = "0";
			field[12] = "0";
			field[13] = "";
			new TableItem(table, SWT.NONE).setText(field);
		}
		composite_controller.layout();
	}
	
	static private void step4(final String type, final String[] char_list, final Table table) {
		selectStep(4);
		
		// 정보 수정 및 새로운 아이템 추가.
		for(TableItem ti : table.getItems()){
//			int item_objectid = Integer.valueOf(ti.getText(0));
			String name = ti.getText(1);
			int count = Integer.valueOf(ti.getText(2));
			int quantity = Integer.valueOf(ti.getText(3));
			int en = Integer.valueOf(ti.getText(4));
//			boolean equipped = Boolean.valueOf(ti.getText(5));
			boolean definite = Boolean.valueOf(ti.getText(6));
			int bress = Integer.valueOf(ti.getText(7));
			int durability = Integer.valueOf(ti.getText(8));
			int nowtime = Integer.valueOf(ti.getText(9));
			int pet_objid = Integer.valueOf(ti.getText(10));
			int inn_key = Integer.valueOf(ti.getText(11));
			int letter_uid = Integer.valueOf(ti.getText(12));
			String race = ti.getText(13);
			
			Item item = ItemDatabase.find(name);
			if(item != null) {
				Connection con = null;
				try {
					con = DatabaseConnection.getLineage();
					//
					if(type.equalsIgnoreCase("검색")) {
						for(String char_name : char_list) {
							if(CharactersDatabase.isCharacterName(con, char_name)) {
								PcInstance pc = World.findPc(char_name);
								if(pc != null) {
									toAppendItemPlayer(pc, item, count, quantity, en, definite, bress, durability, nowtime, pet_objid, inn_key, letter_uid, race);
								} else {
									//
									toAppendItemDb(con, char_name, item, count, quantity, en, definite, bress, durability, nowtime, pet_objid, inn_key, letter_uid, race);
								}
							}
						}
					}
					if(type.equalsIgnoreCase("접속")) {
						for(PcInstance pc : World.getPcList())
							toAppendItemPlayer(pc, item, count, quantity, en, definite, bress, durability, nowtime, pet_objid, inn_key, letter_uid, race);
					}
					if(type.equalsIgnoreCase("전체")) {
						//
						java.util.List<String> list = new ArrayList<String>();
						CharactersDatabase.getNameAllList(con, list);
						for(String char_name : list)
							toAppendItemDb(con, char_name, item, count, quantity, en, definite, bress, durability, nowtime, pet_objid, inn_key, letter_uid, race);
					}
					
				} catch (Exception e) {
				} finally {
					DatabaseConnection.close(con);
				}
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
		label_step4.setForeground(step==4 ? SWTResourceManager.getColor(SWT.COLOR_DARK_RED) : SWTResourceManager.getColor(SWT.COLOR_BLACK));
		
		label_step1.setFont(step==1 ? select : normal);
		label_step2.setFont(step==2 ? select : normal);
		label_step3.setFont(step==3 ? select : normal);
		label_step4.setFont(step==4 ? select : normal);
	}
	
	/**
	 * 사용자 검색
	 * @param name
	 */
	static private void toSearchPlayer(Text text, List list) {
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			String name = text.getText().toLowerCase();
			
			// 이전 기록 제거
			list.removeAll();
			// 디비 케릭이름 추출.
			list_search_name.clear();
			CharactersDatabase.getNameAllList(con, list_search_name);

			if(name!=null && name.length()>0) {
				for(String s : list_search_name) {
					if(s.indexOf(name) >= 0)
						list.add( s );
				}
			} else {
				// 월드 케릭이름 추출.
				for(String s : list_search_name)
					list.add( s );
			}
			
		} catch (Exception e) {
		} finally {
			DatabaseConnection.close(con);
		}
		
		// 등록된게 없을경우 안내 멘트.
		if(list.getItemCount() <= 0)
			GuiMain.toMessageBox(title, "검색된 사용자가 없습니다.");
		
		// 포커스.
		text.setFocus();
	}
	
	/**
	 * 아이템 검색
	 * @param text
	 * @param list
	 */
	static private void toSearchItem(Text text, List list){
		String name = text.getText().toLowerCase();
		
		// 이전 기록 제거
		list.removeAll();
		
		// 검색명이 없을경우 전체 표현.
		if(name==null || name.length()<=0){
			if(ItemDatabase.getList().size() > 0){
				for(Item i : ItemDatabase.getList())
					list.add( i.getName() );
			}else{
				GuiMain.toMessageBox(title, "아이템이 존재하지 않습니다.");
			}
			return;
		}
		
		// 검색.
		for(Item i : ItemDatabase.getList()){
			int pos = i.getName().toLowerCase().indexOf(name);
			if(pos >= 0)
				list.add( i.getName() );
		}
		
		// 등록된게 없을경우 안내 멘트.
		if(list.getItemCount() <= 0)
			GuiMain.toMessageBox(title, "일치하는 아이템이 없습니다.");
		
		// 포커스.
		text.setFocus();
	}
	
	static private void toAppendItemPlayer(PcInstance pc, Item item, long count, int quantity, int en, boolean definite, int bress, int durability, int nowtime, long pet_objid, long inn_key, int letter_uid, String race) {
		ItemInstance ii = ItemDatabase.newInstance(item);
		ii.setObjectId(ServerDatabase.nextItemObjId());
		ii.setCount(count);
		ii.setQuantity(quantity);
		ii.setEnLevel(en);
		ii.setDefinite(definite);
		ii.setBless(bress);
		ii.setDurability(durability);
		ii.setTime(nowtime);
		ii.setPetObjectId(pet_objid);
		ii.setInnRoomKey(inn_key);
		ii.setLetterUid(letter_uid);
		ii.setRaceTicket(race);
		pc.getInventory().append(ii, ii.getCount(), "type|GM지급");
		// \f1%0%s 당신에게 %1%o 주었습니다.
		pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, ServerDatabase.getName(), ii.toString()));
		//
		ItemDatabase.setPool(ii);
	}
	
	static private void toAppendItemDb(Connection con, String char_name, Item item, long count, int quantity, int en, boolean definite, int bress, int durability, int nowtime, long pet_objid, long inn_key, int letter_uid, String race) {
		//
		ItemInstance ii = ItemDatabase.newInstance(item);
		ii.setObjectId(ServerDatabase.nextItemObjId());
		ii.setCount(count);
		ii.setQuantity(quantity);
		ii.setEnLevel(en);
		ii.setDefinite(definite);
		ii.setBless(bress);
		ii.setDurability(durability);
		ii.setTime(nowtime);
		ii.setPetObjectId(pet_objid);
		ii.setInnRoomKey(inn_key);
		ii.setLetterUid(letter_uid);
		ii.setRaceTicket(race);
		//
		ItemDatabase.setPool(ii);
	}
	
}
