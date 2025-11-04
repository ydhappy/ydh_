package lineage.gui;

import com.swtdesigner.SWTResourceManager;

import system.Gui_System;
import lineage.Main;
import lineage.bean.lineage.Kingdom;
import lineage.database.BackgroundDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.FishItemListDatabase;
import lineage.database.HackNoCheckDatabase;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemChanceBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ItemSkillDatabase;
import lineage.database.ItemTeleportDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcDatabase;
import lineage.database.PolyDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.SummonListDatabase;
import lineage.database.TimeDungeonDatabase;
import lineage.gui.composite.ConsoleComposite;
import lineage.gui.composite.ViewComposite;
import lineage.gui.dialog.PlayerItemAppend;
import lineage.plugin.PluginController;
import lineage.share.Admin;
import lineage.share.Lineage;
import lineage.share.Socket;
import lineage.util.Shutdown;
import lineage.world.World;
import lineage.world.controller.AutoHuntController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.EventController;
import lineage.world.controller.ExpMarbleController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.NoticeController;
import lineage.world.controller.RobotController;
import lineage.world.controller.RobotClanController;
import lineage.world.object.instance.PcInstance;
import java.sql.Connection;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import all_night.Lineage_Balance;
import all_night.Npc_promotion;
import all_night.util.Monster_Drop_sql;
import all_night.util.Monster_spawnlist_sql;
import all_night.util.Spr_Action_sql;
import goldbitna.RobotTalkDAO;

public final class GuiMain {

	// gui 컴포넌트들.
	static public Display display;
	static public Shell shell;
	static private ViewComposite viewComposite;
	static private ConsoleComposite consoleComposite;
	static private MenuItem menu_system_1_item_1; // 서버가동
	static private MenuItem menu_system_1_item_2; // 서버종료
	static private MenuItem event; // 이벤트
	static private MenuItem event_menu_1; // 변신 이벤트
	static private MenuItem servercheck; // 점검
	static private MenuItem command; // 명령어
	static private MenuItem datacommand; // 데이터
	static private MenuItem gmcommand; // 명령어
	static private MenuItem monstercommand; // 몬스터
	static private MenuItem integratedreload; // 통합 리로드
	static private MenuItem confreload; // 리니지콘프 리로드
	static private MenuItem balancereload; // 밸런스콘프 리로드
	static private MenuItem adminreload; // 어드콘프 리로드
	static private MenuItem promotionreload; // 엔피씨콘프 리로드
	static private MenuItem socketreload; // 데콘프 리로드
	static private MenuItem bookmarklistreload; // 데콘프 리로드
	static private MenuItem fwordreload; // 데콘프 리로드
	static private MenuItem robotdata;
	static private MenuItem timemanagement; // 시간
	static private MenuItem confmanagement; // conf
	static private MenuItem robotcommand; // 로봇
	static private MenuItem pk1_robotcommand; // PK1 로봇
	static private MenuItem party_robotcommand; // 파티 로봇
	static private MenuItem sieging; // 공성전
	static private MenuItem reload; // 리로드
	static private MenuItem menuItem_5; // 자동버프 이벤트
	static private MenuItem menuItem_7; // 환상 이벤트
	static private MenuItem menuItem_8; // 크리스마스 이벤트
	static private MenuItem menuItem_9; // 할로윈 이벤트
	static private MenuItem menuItem_10; // 토템 이벤트
	static private MenuItem menuItem_11; // 꼬꼬마인형 이벤트
	static private MenuItem menuItem_12; // 헤이스트
	// 리로드
	static private MenuItem mntmNewItem_20;
	static private MenuItem mntmNewItem_21;
	static private MenuItem mntmNewItem_22;
	static private MenuItem mntmNewItem_23;
	static private MenuItem mntmNewItem_24;
	static private MenuItem mntmNewItem_25;
	static private MenuItem mntmNewItem_26;
	static private MenuItem mntmNewItem_27;

	// 컴포트
	static private Label cpu;
	static private Label memory;
	static private Label thread;
	static private Label usercount;
	static private Label server_operating_time;

	// 공성전
	static private MenuItem sieging1;
	static private MenuItem sieging2;
	static private MenuItem sieging3;
	static private MenuItem sieging4;
	static private MenuItem sieging5;
	static private MenuItem sieging6;
	static private MenuItem sieging7;
	static private MenuItem sieging8;
	static private MenuItem sieging9;
	static private MenuItem sieging10;
	static private MenuItem sieging11;
	static private MenuItem sieging12;
	static private MenuItem sieging13;
	static private MenuItem sieging14;
	
	// 서버팩 버전
	static public final String SERVER_VERSION = " Ver 0.1";
	// 클라이언트 접속 최대치값.
	static public int CLIENT_MAX = 5000; // 500

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	static public void open() {
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(950, 748);
		shell.setText(String.format("Lineage %s by goldbitna", new Object[] { "2.0" }));
		shell.setImage(SWTResourceManager.getImage("images/icon.ico"));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		shell.setLayout((Layout) gridLayout);

		shell.addListener(SWT.Close, event -> {
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setText("서버 종료");
			messageBox.setMessage("정말로 종료하시겠습니까?");

			if (messageBox.open() != SWT.YES) {
				event.doit = false; // 닫기 취소
			}
		});

		Label label1 = new Label((Composite) shell, 0);
		label1.setBounds(50, 50, 950, 70);
		Image image1 = new Image((Device) display, "images/image.png");
		label1.setImage(image1);
		new Label((Composite) shell, 0);
		Composite composite = new Composite((Composite) shell, 0);
		GridData gridData1 = new GridData(16384, 16777216, false, false, 1, 1);
		gridData1.widthHint = 930;
		gridData1.heightHint = 30;
		composite.setLayoutData(gridData1);
		composite.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		composite.setBounds(0, 524, 784, 37);

		Label label2 = new Label(composite, 0);
		Image image2 = SWTResourceManager.getImage("images/Cpu.png");
		label2.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label2.setBounds(17, 5, 20, 19);
		label2.setImage(image2);

		Label label3 = new Label(composite, 0);
		label3.setToolTipText("컴퓨터 CPU 점유율 사용량.");
		label3.setForeground(new Color((Device) Display.getCurrent(), 64, 64, 64));
		label3.setFont(SWTResourceManager.getFont("맑은 고딕", 9, 1));
		label3.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label3.setBounds(40, 8, 34, 19);
		label3.setText("CPU :");

		Label label4 = new Label(composite, 0);
		Image image3 = SWTResourceManager.getImage("images/Ram.png");
		label4.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label4.setBounds(135, 6, 20, 19);
		label4.setImage(image3);
		Label label5 = new Label(composite, 0);
		label5.setToolTipText("서버 메모리 사용량.");
		label5.setForeground(new Color((Device) Display.getCurrent(), 64, 64, 64));
		label5.setFont(SWTResourceManager.getFont("맑은 고딕", 9, 1));
		label5.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label5.setBounds(160, 8, 66, 19);
		label5.setText("MEMORY :");
		Label label6 = new Label(composite, 0);
		Image image4 = SWTResourceManager.getImage("images/Thread.png");
		label6.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label6.setBounds(453, 5, 20, 19);
		label6.setImage(image4);
		Label label7 = new Label(composite, 0);
		label7.setToolTipText("서버 스레드 사용량.");
		label7.setForeground(new Color((Device) Display.getCurrent(), 64, 64, 64));
		label7.setFont(SWTResourceManager.getFont("맑은 고딕", 9, 1));
		label7.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label7.setBounds(475, 8, 57, 19);
		label7.setText("THREAD :");
		cpu = new Label(composite, 0);
		cpu.setForeground(SWTResourceManager.getColor(2));
		cpu.setFont(SWTResourceManager.getFont("맑은 고딕", 10, 0));
		cpu.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		cpu.setBounds(80, 7, 40, 19);
		cpu.setText(String.format("%.0f%%", new Object[] { Double.valueOf(Gui_System.getUseCpu()) }));
		memory = new Label(composite, 0);
		memory.setForeground(SWTResourceManager.getColor(2));
		memory.setFont(SWTResourceManager.getFont("맑은 고딕", 10, 0));
		memory.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		memory.setBounds(230, 7, 230, 19);
		memory.setText(String.format("%d", new Object[] { Long.valueOf(Gui_System.getToTalMemoryMB()) }) + "MB / " + String.format("%d", new Object[] { Long.valueOf(Gui_System.getMemoryMB()) }) + "MB / "
				+ String.format("%d", new Object[] { Long.valueOf(Gui_System.getFreeMemoryMB()) }) + "MB");
		thread = new Label(composite, 0);
		thread.setForeground(SWTResourceManager.getColor(2));
		thread.setFont(SWTResourceManager.getFont("맑은 고딕", 10, 0));
		thread.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		thread.setBounds(538, 7, 40, 19);
		thread.setText(String.format("%d", new Object[] { Integer.valueOf(Gui_System.getThread()) }));
		Label label8 = new Label(composite, 0);
		Image image5 = SWTResourceManager.getImage("images/User.png");
		label8.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label8.setBounds(590, 6, 20, 19);
		label8.setImage(image5);
		Label label9 = new Label(composite, 0);
		label9.setToolTipText("서버 접속자 수.");
		label9.setForeground(new Color((Device) Display.getCurrent(), 64, 64, 64));
		label9.setFont(SWTResourceManager.getFont("맑은 고딕", 9, 1));
		label9.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		label9.setBounds(614, 8, 60, 19);
		label9.setText("접속자 수 :");
		usercount = new Label(composite, 0);
		usercount.setForeground(SWTResourceManager.getColor(2));
		usercount.setFont(SWTResourceManager.getFont("맑은 고딕", 10, 0));
		usercount.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		usercount.setBounds(682, 7, 55, 19);
		usercount.setText("0");
		Label label10 = new Label(composite, 0);
		Image image6 = SWTResourceManager.getImage("images/Timer.png");
		label10.setBounds(750, 6, 20, 19);
		label10.setImage(image6);
		server_operating_time = new Label(composite, 0);
		server_operating_time.setToolTipText("서버 구동 시간 확인.");
		server_operating_time.setForeground(SWTResourceManager.getColor(2));
		server_operating_time.setFont(SWTResourceManager.getFont("맑은 고딕", 9, 0));
		server_operating_time.setBackground(new Color((Device) Display.getCurrent(), 230, 230, 230));
		server_operating_time.setBounds(770, 8, 150, 19);
		server_operating_time.setText("정지 상태: 0 day 00:00:00");
		new Label((Composite) shell, 0);

		Label label11 = new Label((Composite) shell, 0);
		Image image7 = SWTResourceManager.getImage("images/Conditions_s.png");
		label11.setBounds(0, 0, 930, 5);
		label11.setImage(image7);

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem menu_system = new MenuItem(menu, SWT.CASCADE);
		menu_system.setText("시스템");

		Menu menu_system_1 = new Menu(menu_system);
		menu_system.setMenu(menu_system_1);

		menu_system_1_item_1 = new MenuItem(menu_system_1, SWT.NONE);
		menu_system_1_item_1.setText("서버 ON");

		menu_system_1_item_2 = new MenuItem(menu_system_1, SWT.CASCADE);
		menu_system_1_item_2.setText("서버 OFF");
		menu_system_1_item_2.setEnabled(false);

		Menu serverOffMenu = new Menu(menu_system_1_item_2);
		menu_system_1_item_2.setMenu(serverOffMenu);

		MenuItem serverOffMenu_1 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_2 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_3 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_4 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_5 = new MenuItem(serverOffMenu, SWT.CHECK);
		MenuItem serverOffMenu_6 = new MenuItem(serverOffMenu, SWT.CHECK);
		serverOffMenu_6.setEnabled(false);

		new MenuItem(menu_system_1, SWT.SEPARATOR);

		serverOffMenu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lineage.share.System.println("서버가 즉시 종료됩니다...");
				Main.close();
				serverOffMenu_1.setSelection(true);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);
				menu_system_1_item_2.setEnabled(false);

				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
				serverOffMenu_6.setEnabled(false);
			}
		});
		serverOffMenu_1.setText("1.   즉시 서버 종료");

		serverOffMenu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;

				new Thread(Shutdown.getInstance(20)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(true);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);

				serverOffMenu_6.setEnabled(true);

				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_2.setText("2.   20초 후 서버 종료");

		serverOffMenu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;

				new Thread(Shutdown.getInstance(60)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(true);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);

				serverOffMenu_6.setEnabled(true);

				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_3.setText("3.   1분 후 서버 종료");

		serverOffMenu_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;

				new Thread(Shutdown.getInstance(60 * 5)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(true);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);

				serverOffMenu_6.setEnabled(true);

				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_4.setText("4.   5분 후 서버 종료");

		serverOffMenu_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;

				new Thread(Shutdown.getInstance(60 * 10)).start();
				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(true);
				serverOffMenu_6.setSelection(false);

				serverOffMenu_6.setEnabled(true);

				serverOffMenu_1.setEnabled(false);
				serverOffMenu_2.setEnabled(false);
				serverOffMenu_3.setEnabled(false);
				serverOffMenu_4.setEnabled(false);
				serverOffMenu_5.setEnabled(false);
			}
		});
		serverOffMenu_5.setText("5.   10분 후 서버 종료");

		serverOffMenu_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Shutdown.getInstance() != null)
					Shutdown.getInstance().is_shutdown = false;

				serverOffMenu_1.setSelection(false);
				serverOffMenu_2.setSelection(false);
				serverOffMenu_3.setSelection(false);
				serverOffMenu_4.setSelection(false);
				serverOffMenu_5.setSelection(false);
				serverOffMenu_6.setSelection(false);

				serverOffMenu_1.setEnabled(true);
				serverOffMenu_2.setEnabled(true);
				serverOffMenu_3.setEnabled(true);
				serverOffMenu_4.setEnabled(true);
				serverOffMenu_5.setEnabled(true);
				serverOffMenu_6.setEnabled(false);
			}
		});
		serverOffMenu_6.setText("6.   서버 종료 취소");

		MenuItem menuItem_6 = new MenuItem(menu_system_1, SWT.NONE);
		menuItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Tray tray = display.getSystemTray();
				if (tray != null) {
					// 현재 윈도우 감추기.
					shell.setVisible(false);
					// 트레이 활성화.
					final TrayItem item = new TrayItem(tray, SWT.NONE);
					item.setToolTipText(String.format("%s : %d", SERVER_VERSION, Lineage.server_version));
					item.setImage(SWTResourceManager.getImage("images/icon.ico"));
					// 이벤트 등록.
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							item.dispose();
							shell.setVisible(true);
							shell.setFocus();
						}
					});
				}
			}
		});
		menuItem_6.setText("최소 창모드");

		///////////////////////////////////////////////////////////////////////
		MenuItem servercheck_menu = new MenuItem(menu, SWT.CASCADE);
		servercheck_menu.setText("서버 관리");

		Menu servercheck_menu1 = new Menu(servercheck_menu);
		servercheck_menu.setMenu(servercheck_menu1);

		servercheck = new MenuItem(servercheck_menu1, SWT.CASCADE);
		servercheck.setEnabled(false);
		servercheck.setText("서버 점검");

		Menu servercheck_menu2 = new Menu(servercheck);
		servercheck.setMenu(servercheck_menu2);

		MenuItem servercheck_menu_1 = new MenuItem(servercheck_menu2, SWT.CHECK);
		servercheck_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverworkOpenWait();
				servercheck_menu_1.setSelection(true);
			}
		});
		servercheck_menu_1.setText("시작");

		MenuItem servercheck_menu_2 = new MenuItem(servercheck_menu2, SWT.NONE);
		servercheck_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverorkOpen();
				servercheck_menu_1.setSelection(false);
			}
		});
		servercheck_menu_2.setText("종료");
		///////////////////////////////////////////////////////////////////////
		MenuItem menu_robot = new MenuItem(menu, SWT.CASCADE);
		menu_robot.setText("로봇 관리");

		Menu robot_menu1 = new Menu(menu_robot);
		menu_robot.setMenu(robot_menu1);

		robotcommand = new MenuItem(robot_menu1, SWT.CASCADE);
		robotcommand.setEnabled(false);
		robotcommand.setText("AI 로봇");

		Menu robotcommand_menu = new Menu(robotcommand);
		robotcommand.setMenu(robotcommand_menu);

		MenuItem robotcommand_menu_1 = new MenuItem(robotcommand_menu, SWT.CHECK);
		robotcommand_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPcRobot(false);
				robotcommand_menu_1.setSelection(true);
			}
		});
		robotcommand_menu_1.setText("스폰");
		
		MenuItem robotcommand_menu_2 = new MenuItem(robotcommand_menu, SWT.NONE);
		robotcommand_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPcRobot(true);
				robotcommand_menu_1.setSelection(false);
			}
		});
		robotcommand_menu_2.setText("제거");
		//////////////////////////////////////////////////////////////////////		
		pk1_robotcommand = new MenuItem(robot_menu1, SWT.CASCADE);
		pk1_robotcommand.setEnabled(false);
		pk1_robotcommand.setText("AI PK 로봇");

		Menu pk1_robotcommand_menu = new Menu(pk1_robotcommand);
		pk1_robotcommand.setMenu(pk1_robotcommand_menu);

		MenuItem pk1_robotcommand_menu_1 = new MenuItem(pk1_robotcommand_menu, SWT.CHECK);
		pk1_robotcommand_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPkRobot(false);
				pk1_robotcommand_menu_1.setSelection(true);
			}
		});
		pk1_robotcommand_menu_1.setText("스폰");
		
		MenuItem pk1_robotcommand_menu_2 = new MenuItem(pk1_robotcommand_menu, SWT.NONE);
		pk1_robotcommand_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPkRobot(true);
				pk1_robotcommand_menu_1.setSelection(false);
			}
		});
		pk1_robotcommand_menu_2.setText("제거");
		//////////////////////////////////////////////////////////////////////		
		party_robotcommand = new MenuItem(robot_menu1, SWT.CASCADE);
		party_robotcommand.setEnabled(false);
		party_robotcommand.setText("AI 파티 로봇");

		Menu party_robotcommand_menu = new Menu(party_robotcommand);
		party_robotcommand.setMenu(party_robotcommand_menu);

		MenuItem party_robotcommand_menu_1 = new MenuItem(party_robotcommand_menu, SWT.CHECK);
		party_robotcommand_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPartyRobot(false);
				party_robotcommand_menu_1.setSelection(true);
			}
		});
		party_robotcommand_menu_1.setText("스폰");
		
		MenuItem party_robotcommand_menu_2 = new MenuItem(party_robotcommand_menu, SWT.NONE);
		party_robotcommand_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RobotController.reloadPartyRobot(true);
				party_robotcommand_menu_1.setSelection(false);
			}
		});
		party_robotcommand_menu_2.setText("제거");
		//////////////////////////////////////////////////////////////////////
		robotdata = new MenuItem(robot_menu1, SWT.CASCADE);
		robotdata.setEnabled(false);
		robotdata.setText("로봇 데이터");

		Menu robotdata_menu = new Menu(robotdata);
		robotdata.setMenu(robotdata_menu);
		
		final MenuItem mntmrobotdata = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotController.reloadPcRobot();
			}
		});
		mntmrobotdata.setText("_robot 테이블 리로드");
		
		final MenuItem mntmrobotdata1 = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata1.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotController.reloadRobotBook();					
					RobotController.reloadPoly();
					RobotController.reloadRobotSkill();
					RobotController.reloadDrop();
					RobotController.reloadMent();
			}
		});
		mntmrobotdata1.setText("_robot_book 테이블 리로드");
		
		final MenuItem mntmrobotdata2 = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata2.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotController.reloadPoly();
			}
		});
		mntmrobotdata2.setText("_robot_poly 테이블 리로드");
		
		final MenuItem mntmrobotdata3 = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata3.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotController.reloadRobotSkill();
			}
		});
		mntmrobotdata3.setText("_robot_skill 테이블 리로드");
		
		final MenuItem mntmrobotdata4 = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata4.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotController.reloadDrop();
			}
		});
		mntmrobotdata4.setText("_robot_drop 테이블 리로드");
		
		final MenuItem mntmrobotdata5 = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata5.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotController.reloadMent();
			}
		});
		mntmrobotdata5.setText("_robot_ment 테이블 리로드");

		final MenuItem mntmrobotdata6 = new MenuItem(robotdata_menu, SWT.NONE);
		mntmrobotdata6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmrobotdata6.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					RobotTalkDAO.reload();
			}
		});
		mntmrobotdata6.setText("_robot_talk 테이블 리로드");		
		//////////////////////////////////////////////////////////////////////
		MenuItem menu_lineage = new MenuItem(menu, SWT.CASCADE);
		menu_lineage.setText("제어 관리");
		
		Menu commandAndEvent = new Menu(menu_lineage);
		menu_lineage.setMenu(commandAndEvent);
		
		command = new MenuItem(commandAndEvent, SWT.CASCADE);
		command.setEnabled(false);
		command.setText("오픈 관리");
		
		Menu command_menu = new Menu(command);
		command.setMenu(command_menu);
		
		MenuItem command_menu_1 = new MenuItem(command_menu, SWT.CHECK);
		command_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverOpenWait();
				command_menu_1.setSelection(true);
			}
		});
		command_menu_1.setText("대기");
		
		MenuItem command_menu_2 = new MenuItem(command_menu, SWT.NONE);
		command_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.serverOpen();
				command_menu_1.setSelection(false);
			}
		});
		command_menu_2.setText("시작");
//////////////////////////////////////////////////////////////////////
		gmcommand = new MenuItem(commandAndEvent, SWT.CASCADE);
		gmcommand.setEnabled(false);
		gmcommand.setText("명령어");

		Menu Gmcommand_menu = new Menu(gmcommand);
		gmcommand.setMenu(Gmcommand_menu);

		MenuItem mntmNewItem_1 = new MenuItem(Gmcommand_menu, SWT.NONE);
		mntmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PlayerItemAppend.open();
			}
		});
		mntmNewItem_1.setText("아이템 지급");
		
		MenuItem mntmNewItem_2 = new MenuItem(Gmcommand_menu, SWT.NONE);
		mntmNewItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toBuffAll(null);
			}
		});
		mntmNewItem_2.setText("전체 올 버프");
		
		MenuItem mntmNewItem_3 = new MenuItem(Gmcommand_menu, SWT.NONE);
		mntmNewItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toWorldItemClear(null);
			}
		});
		mntmNewItem_3.setText("월드맵 청소");
		
		////////////////////////////////////////////////////////////
		event = new MenuItem(commandAndEvent, SWT.CASCADE);
		event.setEnabled(false);
		event.setText("이벤트");

		Menu event_menu = new Menu(event);
		event.setMenu(event_menu);
		
		event_menu_1 = new MenuItem(event_menu, SWT.CHECK);
		event_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toPoly(event_menu_1.getSelection());
			}
		});
		event_menu_1.setText("변신 이벤트");

		menuItem_9 = new MenuItem(event_menu, SWT.CHECK);
		menuItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toHalloween(menuItem_9.getSelection());

			}
		});
		menuItem_9.setText("할로윈 이벤트");
		
		menuItem_8 = new MenuItem(event_menu, SWT.CHECK);
		menuItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toChristmas(menuItem_8.getSelection());
			}
		});
		menuItem_8.setText("크리스마스 이벤트");
		
		menuItem_7 = new MenuItem(event_menu, SWT.CHECK);
		menuItem_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toIllusion(menuItem_7.getSelection());
			}
		});
		menuItem_7.setText("수렵 이벤트");
		
		menuItem_11 = new MenuItem(event_menu, SWT.CHECK);
		menuItem_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventController.toLittlefairy(menuItem_11.getSelection());
			}
		});
		menuItem_11.setText("꼬꼬마인형 이벤트");
		
		menuItem_12 = new MenuItem(event_menu, SWT.CHECK);
		menuItem_12.setData("previousState", false);  // 초기 상태를 false로 설정

		menuItem_12.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        boolean currentState = menuItem_12.getSelection();
		        boolean previousState = (boolean) menuItem_12.getData("previousState");

		        if (currentState != previousState) {  // 상태가 변경되었을 때만 리셋
		            EventController.toautobuff(currentState);
		            menuItem_12.setData("previousState", currentState);  // 이전 상태 업데이트
		        }
		    }
		});

		menuItem_12.setText("추억의 헤이샵 이벤트");
		
		sieging = new MenuItem(commandAndEvent, SWT.CASCADE);
		sieging.setEnabled(false);
		sieging.setText("공성전 관리");

		Menu sieging_menu_1 = new Menu(sieging);
		sieging.setMenu(sieging_menu_1);

		MenuItem mntmNew_1 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_1.setText("켄트성");

		Menu sieging_menu_2 = new Menu(mntmNew_1);
		mntmNew_1.setMenu(sieging_menu_2);

		sieging1 = new MenuItem(sieging_menu_2, SWT.NONE);
		sieging1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(1);
			}
		});
		sieging1.setText("시작");

		sieging2 = new MenuItem(sieging_menu_2, SWT.NONE);
		sieging2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(1);
			}
		});
		sieging2.setText("종료");

		MenuItem mntmNew_2 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_2.setText("오크 요새");

		Menu sieging_menu_3 = new Menu(mntmNew_2);
		mntmNew_2.setMenu(sieging_menu_3);

		sieging3 = new MenuItem(sieging_menu_3, SWT.NONE);
		sieging3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(2);
			}
		});
		sieging3.setText("시작");

		sieging4 = new MenuItem(sieging_menu_3, SWT.NONE);
		sieging4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(2);
			}
		});
		sieging4.setText("종료");

		MenuItem mntmNew_3 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_3.setText("윈다우드 성");

		Menu sieging_menu_4 = new Menu(mntmNew_3);
		mntmNew_3.setMenu(sieging_menu_4);

		sieging5 = new MenuItem(sieging_menu_4, SWT.NONE);
		sieging5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(3);
			}
		});
		sieging5.setText("시작");

		sieging6 = new MenuItem(sieging_menu_4, SWT.NONE);
		sieging6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(3);
			}
		});
		sieging6.setText("종료");

		MenuItem mntmNew_4 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_4.setText("기란 성");

		Menu sieging_menu_5 = new Menu(mntmNew_4);
		mntmNew_4.setMenu(sieging_menu_5);

		sieging7 = new MenuItem(sieging_menu_5, SWT.NONE);
		sieging7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(4);
			}
		});
		sieging7.setText("시작");

		sieging8 = new MenuItem(sieging_menu_5, SWT.NONE);
		sieging8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(4);
			}
		});
		sieging8.setText("종료");

		MenuItem mntmNew_5 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_5.setText("하이네 성");

		Menu sieging_menu_6 = new Menu(mntmNew_5);
		mntmNew_5.setMenu(sieging_menu_6);

		sieging9 = new MenuItem(sieging_menu_6, SWT.NONE);
		sieging9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(5);
			}
		});
		sieging9.setText("시작");

		sieging10 = new MenuItem(sieging_menu_6, SWT.NONE);
		sieging10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(5);
			}
		});
		sieging10.setText("종료");
		
		MenuItem mntmNew_6 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_6.setText("지저 성");

		Menu sieging_menu_7 = new Menu(mntmNew_6);
		mntmNew_6.setMenu(sieging_menu_7);

		sieging11 = new MenuItem(sieging_menu_7, SWT.NONE);
		sieging11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(6);
			}
		});
		sieging11.setText("시작");

		sieging12 = new MenuItem(sieging_menu_7, SWT.NONE);
		sieging12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(6);
			}
		});
		sieging12.setText("종료");

		MenuItem mntmNew_7 = new MenuItem(sieging_menu_1, SWT.CASCADE);
		mntmNew_7.setText("아덴 성");

		Menu sieging_menu_8 = new Menu(mntmNew_7);
		mntmNew_7.setMenu(sieging_menu_8);

		sieging13 = new MenuItem(sieging_menu_8, SWT.NONE);
		sieging13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStart(7);
			}
		});
		sieging13.setText("시작");

		sieging14 = new MenuItem(sieging_menu_8, SWT.NONE);
		sieging14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toKingdomWarStop(7);
			}
		});
		sieging14.setText("종료");
		
		datacommand = new MenuItem(commandAndEvent, SWT.CASCADE);
		datacommand.setEnabled(false);
		datacommand.setText("데이터 관리");
		
		Menu datacommand_menu = new Menu(datacommand);
		datacommand.setMenu(datacommand_menu);
		
		MenuItem datacommand_menu_1 = new MenuItem(datacommand_menu, SWT.NONE);
		datacommand_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandController.toBanAllRemove(null);
			}
		});
		datacommand_menu_1.setText("전체 벤 해제");
		
		MenuItem datacommand_menu_2 = new MenuItem(datacommand_menu, SWT.NONE);
		datacommand_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (PcInstance pc : World.getPcList())
					pc.toCharacterSave();
				
				lineage.share.System.println("캐릭터 정보 저장 완료");
			}
		});
		datacommand_menu_2.setText("캐릭터 정보 저장");
		
		MenuItem datacommand_menu_3 = new MenuItem(datacommand_menu, SWT.NONE);
		datacommand_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpMarbleController.resetCount();
			}
		});
		datacommand_menu_3.setText("경험치 저장 구슬");

		timemanagement = new MenuItem(commandAndEvent, SWT.CASCADE);
		timemanagement.setEnabled(false);
		timemanagement.setText("이용시간 관리");
		
		Menu timemanagement_menu = new Menu(timemanagement);
		timemanagement.setMenu(timemanagement_menu);
		
		MenuItem timemanagement_menu_1 = new MenuItem(timemanagement_menu, SWT.NONE);
		timemanagement_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AutoHuntController.resetAutoHuntTime();
				
			}
		});
		timemanagement_menu_1.setText("자동사냥 이용시간 초기화");
		
		MenuItem timemanagement_menu_2 = new MenuItem(timemanagement_menu, SWT.NONE);
		timemanagement_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeDungeonDatabase.resetGiranDungeonTime();
				TimeDungeonDatabase.resetCheck();
			}
		});
		timemanagement_menu_2.setText("기란감옥 이용시간 초기화");

		MenuItem timemanagement_menu_3 = new MenuItem(timemanagement_menu, SWT.NONE);
		timemanagement_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TimeDungeonDatabase.resetQuest();
			}
		});
		timemanagement_menu_3.setText("일일 퀘스트 초기화");
		/////////////////////////////////////////////////////////////////////
		confmanagement = new MenuItem(menu, SWT.CASCADE);
		confmanagement.setText("CONF 관리");
		
		Menu confmanagement_menu = new Menu(confmanagement);
		confmanagement.setMenu(confmanagement_menu);

		confreload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		confreload.setEnabled(false);
		confreload.setText("lineage.conf");

		Menu confreload_menu = new Menu(confreload);
		confreload.setMenu(confreload_menu);
		
		MenuItem lineageConf = new MenuItem(confreload_menu, SWT.NONE);
		lineageConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/lineage.conf");
				} catch (Exception e2) { }
			}
		});
		lineageConf.setText("lineage.conf 열기");
		
		final MenuItem mntmLineageconf = new MenuItem(confreload_menu, SWT.NONE);
		mntmLineageconf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmLineageconf.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					Lineage.init(true);
			}
		});
		mntmLineageconf.setText("lineage.conf 저장");

		balancereload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		balancereload.setEnabled(false);
		balancereload.setText("lineage_balance.conf");

		Menu balancereload_menu = new Menu(balancereload);
		balancereload.setMenu(balancereload_menu);
		
		MenuItem balanceconf = new MenuItem(balancereload_menu, SWT.NONE);
		balanceconf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/lineage_balance.conf");
				} catch (Exception e2) { }
			}
		});
		balanceconf.setText("lineage_balance.conf 열기");
		
		final MenuItem mntmbalanceconf = new MenuItem(balancereload_menu, SWT.NONE);
		mntmbalanceconf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmbalanceconf.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					Lineage_Balance.init();
			}
		});
		mntmbalanceconf.setText("lineage_balance.conf 저장");

		// 어드콘프 리로드 메뉴 추가
		adminreload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		adminreload.setEnabled(true);
		adminreload.setText("admin.conf");

		// admin.conf 메뉴 생성
		Menu adminreload_menu = new Menu(adminreload);
		adminreload.setMenu(adminreload_menu);

		// admin.conf 파일 열기 메뉴 아이템
		MenuItem openAdminConf = new MenuItem(adminreload_menu, SWT.NONE);
		openAdminConf.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {        
		        try {
		            Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/admin.conf");
		        } catch (Exception e2) {
		            e2.printStackTrace();
		        }
		    }
		});
		openAdminConf.setText("admin.conf 열기");

		// admin.conf 설정 다시 로드 메뉴 아이템
		final MenuItem reloadAdminConf = new MenuItem(adminreload_menu, SWT.NONE);
		reloadAdminConf.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
		        messageBox.setText("경고");
		        messageBox.setMessage(reloadAdminConf.getText() + "을 다시 읽겠습니까?");
		        
		        if (messageBox.open() == SWT.YES) {
		            Admin.init(); // admin.conf 다시 로드
		        }
		    }
		});
		reloadAdminConf.setText("admin.conf 저장");

		promotionreload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		promotionreload.setEnabled(false);
		promotionreload.setText("npc_promotion.conf");

		Menu promotionreload_menu = new Menu(promotionreload);
		promotionreload.setMenu(promotionreload_menu);
		
		MenuItem promotionreload = new MenuItem(promotionreload_menu, SWT.NONE);
		promotionreload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/npc_promotion.conf");
				} catch (Exception e2) { }
			}
		});
		promotionreload.setText("npc_promotion.conf 열기");
		
		final MenuItem mntmpromotionreload = new MenuItem(promotionreload_menu, SWT.NONE);
		mntmpromotionreload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmpromotionreload.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					Npc_promotion.reload();
			}
		});
		mntmpromotionreload.setText("npc_promotion.conf 저장");

		socketreload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		socketreload.setEnabled(false);
		socketreload.setText("socket.conf");

		Menu socketreload_menu = new Menu(socketreload);
		socketreload.setMenu(socketreload_menu);
		
		MenuItem socketConf = new MenuItem(socketreload_menu, SWT.NONE);
		socketConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/socket.conf");
				} catch (Exception e2) { }
			}
		});
		socketConf.setText("socket.conf 열기");
		
		final MenuItem mntmsocketreload = new MenuItem(socketreload_menu, SWT.NONE);
		mntmsocketreload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmsocketreload.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					Socket.reload();
			}
		});
		mntmsocketreload.setText("socket.conf 저장");

		fwordreload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		fwordreload.setEnabled(false);
		fwordreload.setText("fword_list.txt");

		Menu fwordreload_menu = new Menu(fwordreload);
		fwordreload.setMenu(fwordreload_menu);
		
		MenuItem fword_list = new MenuItem(fwordreload_menu, SWT.NONE);
		fword_list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/goldbitna_word/fword_list.txt");
				} catch (Exception e2) { }
			}
		});
		fword_list.setText("fword_list.txt 열기");
		
		final MenuItem mntmfwordreload = new MenuItem(fwordreload_menu, SWT.NONE);
		mntmfwordreload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmfwordreload.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					ChattingController.reload();
			}
		});
		mntmfwordreload.setText("fword_list.txt 저장");

		bookmarklistreload = new MenuItem(confmanagement_menu, SWT.CASCADE);
		bookmarklistreload.setEnabled(false);
		bookmarklistreload.setText("notice.txt");

		Menu bookmarklistreload_menu = new Menu(bookmarklistreload);
		bookmarklistreload.setMenu(bookmarklistreload_menu);
		
		MenuItem noticeTxt = new MenuItem(bookmarklistreload_menu, SWT.NONE);
		noticeTxt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/notice.txt");
				} catch (Exception e2) { }
			}
		});
		noticeTxt.setText("notice.txt 열기");
		
		final MenuItem mntmbookmarklistreload = new MenuItem(bookmarklistreload_menu, SWT.NONE);
		mntmbookmarklistreload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmbookmarklistreload.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES)
					NoticeController.reload();
			}
		});
		mntmbookmarklistreload.setText("notice.txt 저장");
		
		MenuItem mySqlConf = new MenuItem(confmanagement_menu, SWT.NONE);
		mySqlConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/WINDOWS/system32/notepad.exe " + System.getProperty("user.dir") + "/mysql.conf");
				} catch (Exception e2) { }
			}
		});
		mySqlConf.setText("mysql.conf 실행");
				
		MenuItem menu_database = new MenuItem(menu, SWT.CASCADE);
		menu_database.setText("데이터 리로드");

		Menu reload_menu = new Menu(menu_database);
		menu_database.setMenu(reload_menu);

		integratedreload = new MenuItem(reload_menu, SWT.CASCADE);
		integratedreload.setEnabled(false);
		integratedreload.setText("통합 리로드");

		Menu integratedreload_menu = new Menu(integratedreload);
		integratedreload.setMenu(integratedreload_menu);
		
		MenuItem mntmNewItem_21 = new MenuItem(integratedreload_menu, SWT.NONE);
		mntmNewItem_21.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_21.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						BackgroundDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_21.setText("Background");

		MenuItem mntmNewItem_22 = new MenuItem(integratedreload_menu, SWT.NONE);
		mntmNewItem_22.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_22.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						ItemDatabase.reload();
						ItemBundleDatabase.reload();
						ItemChanceBundleDatabase.reload();
						ItemDropMessageDatabase.reload();
						ItemSkillDatabase.reload();
						ItemTeleportDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_22.setText("Item");

		MenuItem mntmNewItem_23 = new MenuItem(integratedreload_menu, SWT.NONE);
		mntmNewItem_23.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_23.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						NpcDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_23.setText("Npc");

		MenuItem mntmNewItem_24 = new MenuItem(integratedreload_menu, SWT.NONE);
		mntmNewItem_24.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_24.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						MonsterDatabase.reload();
						MonsterDropDatabase.reload();
						MonsterBossSpawnlistDatabase.reload();
						MonsterSkillDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_24.setText("Monster");

		MenuItem mntmNewItem_25 = new MenuItem(integratedreload_menu, SWT.NONE);
		mntmNewItem_25.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_25.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						CommandController.serverMagicReload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_25.setText("Skill");

		MenuItem mntmNewItem_26 = new MenuItem(integratedreload_menu, SWT.NONE);
		mntmNewItem_26.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_26.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						PolyDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_26.setText("Poly");
		//////////////////////////////////////////////////////////////////
		monstercommand = new MenuItem(reload_menu, SWT.CASCADE);
		monstercommand.setEnabled(false);
		monstercommand.setText("개별 리로드");

		Menu monstercommand_menu = new Menu(monstercommand);
		monstercommand.setMenu(monstercommand_menu);

		MenuItem mntmNewItem_9 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_9.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						MonsterSpawnlistDatabase.close();
						MonsterSpawnlistDatabase.init(con);
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_9.setText("MonsterSpawnlist Database");
		
		MenuItem mntmNewItem_10 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_10.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						HackNoCheckDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_10.setText("HackNoCheck Database");
		
		MenuItem mntmNewItem_11 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_11.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						FishItemListDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_11.setText("FishItemList Database");
		
		MenuItem mntmNewItem_12 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_12.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						SpriteFrameDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_12.setText("SpriteFrame Database");
		
		MenuItem mntmNewItem_13 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_13.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						SummonListDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_13.setText("SummonList Database");
		
		MenuItem mntmNewItem_14 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_14.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						TimeDungeonDatabase.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_14.setText("TimeDungeon Database");
		
		MenuItem mntmNewItem_15 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_15.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_15.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						RobotClanController.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_15.setText("RobotClan Controller");
		
		MenuItem mntmNewItem_16 = new MenuItem(monstercommand_menu, SWT.NONE);
		mntmNewItem_16.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(GuiMain.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				messageBox.setText("경고");
				messageBox.setMessage(mntmNewItem_16.getText() + "을 다시 읽겠습니까?");
				if (messageBox.open() == SWT.YES) {
					Connection con = null;
					try {
						con = DatabaseConnection.getLineage();
						KingdomController.reload();
					} catch (Exception e2) {
					} finally {
						DatabaseConnection.close(con);
					}
				}
			}
		});
		mntmNewItem_16.setText("Kingdom Controller");
		//////////////////////////////////////////////////////////////////
		
		MenuItem menu_execute = new MenuItem(menu, SWT.CASCADE);
		menu_execute.setText("실행");
		
		Menu menu_3 = new Menu(menu_execute);
		menu_execute.setMenu(menu_3);
		
		MenuItem navicat = new MenuItem(menu_3, SWT.NONE);
		navicat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/Program Files (x86)/PremiumSoft/Navicat Premium 8.2/navicat.exe");
				} catch (Exception e2) { }
				
				try {
					Runtime.getRuntime().exec("C:/Program Files (x86)/PremiumSoft/Navicat Premium 8.0/navicat.exe");
				} catch (Exception e2) { }
			}
		});
		navicat.setText("나비켓 실행");
		
		MenuItem explorer = new MenuItem(menu_3, SWT.NONE);
		explorer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/Program Files/Internet Explorer/iexplore.exe");
				} catch (Exception e2) { }
			}
		});
		explorer.setText("익스플로러 실행");
		
		MenuItem chrome = new MenuItem(menu_3, SWT.NONE);
		chrome.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				try {
					Runtime.getRuntime().exec("C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
				} catch (Exception e2) { }
			}
		});
		chrome.setText("구글 크롬 실행");

		MenuItem sqlcommand = new MenuItem(menu, SWT.CASCADE);
		sqlcommand.setText("SQL 생성");
		
		Menu sqlcommand_menu = new Menu(sqlcommand);
		sqlcommand.setMenu(sqlcommand_menu);
		
		MenuItem sqlcommand_menu_1 = new MenuItem(sqlcommand_menu, SWT.NONE);
		sqlcommand_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spr_Action_sql.writeSql();
			}
		});
		sqlcommand_menu_1.setText("spr_action.sql 생성");
		
		MenuItem sqlcommand_menu_2 = new MenuItem(sqlcommand_menu, SWT.NONE);
		sqlcommand_menu_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Monster_Drop_sql.writeSql();
			}
		});
		sqlcommand_menu_2.setText("monster_drop.sql 생성");
		
		MenuItem sqlcommand_menu_3 = new MenuItem(sqlcommand_menu, SWT.NONE);
		sqlcommand_menu_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Monster_spawnlist_sql.writeSql();
			}
		});
		sqlcommand_menu_3.setText("monster_spawnlist.sql 생성");

		MenuItem mapcommand = new MenuItem(menu, SWT.CASCADE);
		mapcommand.setText("MAP TEXT 생성");
		
		Menu mapcommand_menu = new Menu(mapcommand);
		mapcommand.setMenu(mapcommand_menu);
		
		MenuItem mapcommand_menu_1 = new MenuItem(mapcommand_menu, SWT.NONE);
		mapcommand_menu_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spr_Action_sql.writeSql();
			}
		});
		sqlcommand_menu_1.setText("spr_action.sql 생성");
		
		viewComposite = new ViewComposite(shell, SWT.NONE);
		viewComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		consoleComposite = new ConsoleComposite(shell, SWT.NONE);
		GridData gd_consoleComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_consoleComposite.heightHint = 140;
		consoleComposite.setLayoutData(gd_consoleComposite);
		
		// 이벤트 등록.
		menu_system_1_item_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 서버 정보 로드.
				Main.init();
				// 맵뷰어 랜더링 시작.
				viewComposite.getScreenRenderComposite().start();
				// 정보 변경.
				menu_system_1_item_1.setEnabled(false);
				menu_system_1_item_2.setEnabled(true);
			}
		});
		menu_system_1_item_2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread(Shutdown.getInstance()).start();
			}
		});
		Label label12 = new Label((Composite) shell, 0);
		label12.setBounds(50, 50, 950, 20);
		Image image8 = new Image((Device) display, "images/Conditions.png");
		label12.setImage(image8);
		label12.addMouseListener((MouseListener) new MouseAdapter() {
			public void mouseUp(MouseEvent param1MouseEvent) {
				Program.launch("https://naver.com/");
			}
		});
		// 매니저를 윈도우화면 가운데 좌표로 변경.
		shell.setBounds((display.getBounds().width / 2) - (shell.getBounds().width / 2), (display.getBounds().height / 2) - (shell.getBounds().height / 2), shell.getBounds().width, shell.getBounds().height);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Exception e) {
			}
		}

		Main.close();
	}

	static public ViewComposite getViewComposite() {
		return viewComposite;
	}

	static public ConsoleComposite getConsoleComposite() {
		return consoleComposite;
	}
	
	static public void toTimer(long time){
		// 뷰어 처리.
		viewComposite.toTimer(time);
		// 실시간 상태
		usercount.setText(String.format("%d", World.getUserSize()));
		memory.setText(String.format("%d", Gui_System.getUsedMemoryMB()) + "MB/"
				+ String.format("%d", Gui_System.getTotalMemoryMB()) + "MB");
		thread.setText(String.format("%d", Gui_System.getThread()));
		cpu.setText(String.format("%.0f%%", Gui_System.getUseCpu()));
		// 초기화 안된 상태.
		if(!event.isEnabled()){
			// 메뉴 활성화.
			event.setEnabled(true);
			servercheck.setEnabled(true);
			command.setEnabled(true);
			gmcommand.setEnabled(true);
			monstercommand.setEnabled(true);
			integratedreload.setEnabled(true);
			confreload.setEnabled(true);
			balancereload.setEnabled(true);
			promotionreload.setEnabled(true);
			socketreload.setEnabled(true);
			bookmarklistreload.setEnabled(true);
			fwordreload.setEnabled(true);
			robotdata.setEnabled(true);
			datacommand.setEnabled(true);
			timemanagement.setEnabled(true);
			confmanagement.setEnabled(true);
			robotcommand.setEnabled(true);
			pk1_robotcommand.setEnabled(true);
			party_robotcommand.setEnabled(true);
			sieging.setEnabled(true);
			reload.setEnabled(true);
			//리로드
			mntmNewItem_20.setEnabled(true);
			mntmNewItem_21.setEnabled(true);
			mntmNewItem_22.setEnabled(true);
			mntmNewItem_23.setEnabled(true);
			mntmNewItem_24.setEnabled(true);
			mntmNewItem_25.setEnabled(true);
			mntmNewItem_26.setEnabled(true);
			mntmNewItem_27.setEnabled(true);
			// Lineage 설정 정보 갱신
			event_menu_1.setSelection( Lineage.event_poly );
			menuItem_5.setSelection( Lineage.event_buff );
			menuItem_7.setSelection( Lineage.event_illusion );
			menuItem_8.setSelection( Lineage.event_christmas );
			menuItem_9.setSelection( Lineage.event_halloween );
			menuItem_10.setSelection( Lineage.event_lyra );
			menuItem_11.setSelection( Lineage.event_littlefairy );
			menuItem_12.setSelection( Lineage.robot_auto_buff );
			
			//
			Kingdom k = KingdomController.find(1);
			if (k != null) {
				sieging1.setEnabled(!k.isWar());
				sieging2.setEnabled(k.isWar());
			}
			k = KingdomController.find(2);
			if (k != null) {
				sieging3.setEnabled(!k.isWar());
				sieging4.setEnabled(k.isWar());
			}
			k = KingdomController.find(3);
			if (k != null) {
				sieging5.setEnabled(!k.isWar());
				sieging6.setEnabled(k.isWar());
			}
			k = KingdomController.find(4);
			if (k != null) {
				sieging7.setEnabled(!k.isWar());
				sieging8.setEnabled(k.isWar());
			}
			k = KingdomController.find(5);
			if (k != null) {
				sieging9.setEnabled(!k.isWar());
				sieging10.setEnabled(k.isWar());
			}
			k = KingdomController.find(6);
			if (k != null) {
				sieging11.setEnabled(!k.isWar());
				sieging12.setEnabled(k.isWar());
			}
			//
			shell.setText(String.format("Server Version %s", SERVER_VERSION, Lineage.server_version));
		}
	}
	

	/**
	 * 공성 시작
	 * 
	 * @param o    요청 객체
	 * @param uid  성의 UID
	 */
	public static void toKingdomWarStart(int uid) {
	    int i = 0;
	    for (Kingdom k : KingdomController.getList()) {

	        if (k.getUid() == uid) { // UID 비교
	            if (k.isWar()) {
	                i = 1;
	                lineage.share.System.println(String.format("%s : 공성이 이미 진행중입니다.", k.getName()));
	                return;
	            } else {
	                k.toStartWar(System.currentTimeMillis());
	                k.setWarDay(Calendar.getInstance().getTime().getTime());

	                i = 1;
	                lineage.share.System.println(String.format("%s : 공성이 시작되었습니다.", k.getName()));
	            }
	        }
	    }
	    if (i == 0)
	        lineage.share.System.println(String.format("UID %d : 존재하지 않는 성입니다.", uid));
	}

	/**
	 * 공성 종료
	 * 
	 * @param o    요청 객체
	 * @param uid  성의 UID
	 */
	public static void toKingdomWarStop(int uid) {
	    int i = 0;
	    for (Kingdom k : KingdomController.getList()) {
	        if (k.getUid() == uid || uid == 0) { // UID 비교 (전체 종료는 uid == 0)
	            if (!k.isWar()) {
	                i = 1;
	                lineage.share.System.println(String.format("%s : 공성이 진행중이지 않습니다.", k.getName()));
	            } else {
	                k.toStopWar(System.currentTimeMillis());
	                i = 1;
	                lineage.share.System.println(String.format("%s : 공성이 종료되었습니다.", k.getName()));
	            }
	        }
	    }
	    if (uid != 0 && i == 0)
	        lineage.share.System.println(String.format("UID %d : 존재하지 않는 성입니다.", uid));
	}

	
	static private void toKingdomControll(int uid, MenuItem a, MenuItem b, boolean isStart) {
		Kingdom k = KingdomController.find(uid);
		if (k == null) {
			toMessageBox("요청하신 Kingdom 정보가 존재하지 않습니다.");
			return;
		}
		if (PluginController.init(GuiMain.class, "toKingdomController", uid, a, b, isStart, k) == null) {
			if (isStart)
				// 현재 시간을 강제로 기입. 그럼 지가 알아서 공성 시작함.
				k.setWarDay(System.currentTimeMillis());
			else
				k.setWarDayEnd(System.currentTimeMillis());
			//
			a.setEnabled(!a.isEnabled());
			b.setEnabled(!b.isEnabled());
		}
	}

	
	/**
	 * 경고창 띄울때 사용.
	 * 
	 * @param msg
	 */
	static public void toMessageBox(final String msg) {
		toMessageBox(SERVER_VERSION, msg);
	}

	static public void toMessageBox(final String title, final String msg) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
		messageBox.setText(String.format("경고 :: %s", title));
		messageBox.setMessage(msg);
		messageBox.open();
	}

	static public void close() {
		new Thread(Shutdown.getInstance()).start();
	}
}
