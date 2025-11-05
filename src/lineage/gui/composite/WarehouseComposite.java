package lineage.gui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.swtdesigner.SWTResourceManager;

import lineage.share.Log;

public class WarehouseComposite extends Composite {
	
	private StyledText chatting;
	private org.eclipse.swt.graphics.Color color_normal = SWTResourceManager.getColor(243, 247, 0);
	private int log_now_pos;
	private Button scroll;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public WarehouseComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(24, 24, 24));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		Group group = new Group(this, SWT.NONE);
		group.setText("창고 로그");
		GridLayout gl_group = new GridLayout(8, false);
		gl_group.verticalSpacing = 0;
		gl_group.horizontalSpacing = 0;
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		scroll = new Button(group, SWT.CHECK);
		GridData gd_scroll = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_scroll.horizontalIndent = 10;
		scroll.setLayoutData(gd_scroll);
		scroll.setText("스크롤락");
		
		chatting = new StyledText(group, SWT.NONE | SWT.V_SCROLL);
		chatting.setBackground(SWTResourceManager.getColor(45, 45, 45));
		chatting.setDoubleClickEnabled(false);
		chatting.setEditable(false);
		chatting.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

	}

	public void toLog(String msg) {
	    // GUI 영역에 출력
	    Log.append매니저창("창고", msg);

	    msg = String.format("\r\n%s", msg);
	    chatting.append(msg);

	    if (!scroll.getSelection())
	        chatting.setTopIndex(chatting.getVerticalBar().getMaximum());

	    // 색상 구분용 StyleRange 생성
	    StyleRange style = new StyleRange();
	    style.start = log_now_pos;
	    style.length = msg.length();
	    style.fontStyle = SWT.BOLD;

	    // 상태별 색상 지정
	    if (msg.contains("비정상")) {
	        style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	    } else if (msg.contains("실패")) {
	        style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	    } else if (msg.contains("성공") || msg.contains("정상")) {
	        style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	    } else {
	        style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	    }

	    // 스타일 적용
	    chatting.setStyleRange(style);
	    log_now_pos += msg.length();
	}

	
	private StyleRange getHighlightStyle(int startOffset, int length, org.eclipse.swt.graphics.Color color, int font_style) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.foreground = color;
		styleRange.fontStyle = font_style;
		return styleRange;
	}
}
