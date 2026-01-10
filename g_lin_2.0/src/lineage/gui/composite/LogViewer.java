package lineage.gui.composite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import lineage.share.Lineage;
import lineage.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

public class LogViewer extends Composite {

	//
	private Text text;
	private Button btnNewButton;
	private DateTime dateTimeStart;
	private DateTime dateTimeEnd;
	private StyledText styledText;
	private Button btnCheckButton;
	private Button btnCheckButton_1;
	private Button btnCheckButton_2;
	private Button btnCheckButton_3;
	private Button btnCheckButton_4;
	private Button btnCheckButton_5;
	private Button btnNewButton_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LogViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("채팅");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginHeight = 10;
		gl_composite.marginWidth = 10;
		composite.setLayout(gl_composite);
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("검색");
		GridLayout gl_group = new GridLayout(10, false);
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 75;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setText("기간 설정");
		
		dateTimeStart = new DateTime(group, SWT.BORDER);
		GridData gd_dateTime = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_dateTime.horizontalIndent = 20;
		dateTimeStart.setLayoutData(gd_dateTime);
		
		Label label = new Label(group, SWT.NONE);
		label.setText("~");
		
		dateTimeEnd = new DateTime(group, SWT.BORDER);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("검색명");
		
		text = new Text(group, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode==16777296 || e.keyCode==13)
					toSearch();
			}
		});
		text.setFont(SWTResourceManager.getFont("굴림", 9, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		gd_text.heightHint = 16;
		gd_text.horizontalIndent = 20;
		text.setLayoutData(gd_text);
		
		btnCheckButton_5 = new Button(group, SWT.CHECK);
		btnCheckButton_5.setSelection(true);
		btnCheckButton_5.setText("전체");
		
		btnCheckButton = new Button(group, SWT.CHECK);
		btnCheckButton.setSelection(true);
		btnCheckButton.setText("장사");
		
		btnCheckButton_1 = new Button(group, SWT.CHECK);
		btnCheckButton_1.setSelection(true);
		btnCheckButton_1.setText("혈맹");
		
		btnCheckButton_2 = new Button(group, SWT.CHECK);
		btnCheckButton_2.setSelection(true);
		btnCheckButton_2.setText("귓말");
		
		btnCheckButton_4 = new Button(group, SWT.CHECK);
		btnCheckButton_4.setSelection(true);
		btnCheckButton_4.setText("파티");
		
		btnCheckButton_3 = new Button(group, SWT.CHECK);
		btnCheckButton_3.setSelection(true);
		btnCheckButton_3.setText("일반");
		
		btnNewButton_1 = new Button(group, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledText.setText("");
			}
		});
		btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		btnNewButton_1.setText("화면청소");
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		
		btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toSearch();
			}
		});
		GridData gd_btnNewButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1);
		gd_btnNewButton.heightHint = 35;
		gd_btnNewButton.widthHint = 100;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.setText("검색");
		
		Group group_1 = new Group(composite, SWT.NONE);
		group_1.setText("기록");
		group_1.setLayout(new GridLayout(1, false));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		styledText = new StyledText(group_1, SWT.NONE | SWT.V_SCROLL);
		styledText.setBackground(SWTResourceManager.getColor(16, 16, 16));
		styledText.setDoubleClickEnabled(false);
		styledText.setEditable(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void toEnabled(boolean enabled) {
		dateTimeStart.setEnabled(enabled);
		dateTimeEnd.setEnabled(enabled);
		text.setEnabled(enabled);
		btnNewButton.setEnabled(enabled);
		if(enabled)
			text.setFocus();
	}
	
	/**
	 * 검색 시작 메서드.
	 */
	private void toSearch() {
		//
		toEnabled(false);
		try {
			//
			String keyword = text.getText().trim();
//			if(keyword.length() <= 1) {
//				GuiMain.toMessageBox("검색명은 최소 2자 이상만 가능 합니다.");
//				toEnabled(true);
//				return;
//			}
			//
			StringBuffer sb = new StringBuffer();
			//
			int cnt = 0;
			long start_time = Util.getTime( String.format("%04d-%02d-%02d", dateTimeStart.getYear(), dateTimeStart.getMonth()+1, dateTimeStart.getDay()) );
			long end_time = Util.getTime( String.format("%04d-%02d-%02d", dateTimeEnd.getYear(), dateTimeEnd.getMonth()+1, dateTimeEnd.getDay()) );
			for(;start_time<=end_time;start_time+=1000*60*60*24) {
				String dateStr = Util.getLocaleString(start_time, false);
				File file = new File("log/chatting/" + dateStr + ".log");
				if(!file.exists() || !file.isFile())
					continue;
				
				String line = null;
				BufferedReader lnr = new BufferedReader( new FileReader(file)); 
				while( (line = lnr.readLine()) != null) {
					// 못찾은거 무시.
					if(keyword.length()>0 && line.indexOf(keyword) < 0)
						continue;
					String[] lines = line.split("\t");
					//
					if(lines[4].equalsIgnoreCase("null"))
						continue;
					// 카테고리 구분.
					switch(Integer.valueOf(lines[5])) {
						case 0:
							if(!btnCheckButton_3.getSelection())
								continue;
							break;
						case 2:
						case 3:
							if(!btnCheckButton_5.getSelection())
								continue;
							break;
						case 4:
							if(!btnCheckButton_1.getSelection())
								continue;
							break;
						case 9:
							if(!btnCheckButton_2.getSelection())
								continue;
							break;
						case 11:
							if(!btnCheckButton_4.getSelection())
								continue;
							break;
						case 12:
							if(!btnCheckButton.getSelection())
								continue;
							break;
						case 20:
							continue;
					}
					// 기존에 출력된거 무시.
					String log = getLogString(lines);
					if(log==null || styledText.getText().indexOf(log) >= 0)
						continue;
					//
					sb.append(line).append("\r\n");
					//
					if(++cnt > 1000)
						break;
				}
				lnr.close();
				//
				if(cnt > 1000)
					break;
			}
			//
			StringTokenizer st = new StringTokenizer(sb.toString(), "\r\n");
			while(st.hasMoreTokens()) {
				String line = st.nextToken();
				String[] lines = line.split("\t");
				String log = getLogString(lines);
				if(log == null)
					continue;
				styledText.append( log );
				switch(Integer.valueOf(lines[5])) {
					case 0:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_normal, SWT.BOLD) );
						break;
					case 2:
					case 3:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_global, SWT.BOLD) );
						break;
					case 4:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_clan, SWT.BOLD) );
						break;
					case 9:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_whisper, SWT.BOLD) );
						break;
					case 11:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_party, SWT.BOLD) );
						break;
					case 12:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_trade, SWT.BOLD) );
						break;
					case 20:
						styledText.setStyleRange( getHighlightStyle(styledText.getCharCount()-log.length(), log.length(), ChattingComposite.color_message, SWT.BOLD) );
						break;
				}
			}
		} catch (Exception e) {
		}
		//
		toEnabled(true);
	}
	
	private StyleRange getHighlightStyle(int startOffset, int length, org.eclipse.swt.graphics.Color color, int font_style) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.foreground = color;
		styleRange.fontStyle = font_style;
		return styleRange;
	}
	
	private String getLogString(String[] lines) {
		// [2015-03-20 22시55분46초	1426859746815]	127.0.0.1	1111	Nice	0	ㄴㅇㄻㅇㄹㄴ
		try {
			long time = Long.valueOf(lines[1].substring(0, lines[1].length()-1));
			switch(Integer.valueOf(lines[5])) {
				case 2:
				case 3:
				case 12:
					return String.format("[%s %s] [%s] %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[4], lines[6]);
				case 4:
					try {
						return String.format("[%s %s] {%s}{%s} %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[7], lines[4], lines[6]);
					} catch (Exception e) {
						return String.format("[%s %s] {%s} %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[4], lines[6]);
					}
				case 9:
					try {
						return String.format("[%s %s] (%s)->(%s) %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[4], lines[8], lines[6]);
					} catch (Exception e) {
						return String.format("[%s %s] (%s) %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[4], lines[6]);
					}
				case 11:
					return String.format("[%s %s] (%s) %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[4], lines[6]);
				case 20:
				default:
					return String.format("[%s %s] %s : %s\r\n", Util.getLocaleString(time, false), Util.getLocaleString(time), lines[4], lines[6]);
			}
		} catch (Exception e) { }
		return null;
	}
}
