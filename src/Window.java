import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Window <T , E extends Comparator<T>, G extends getNew<T>>extends JFrame {
    BTree<T, E> bTree;
    JTextArea textArea;
    G g;
    PaintBoard paintBoard;
    JPanel p1, p2;

    Window(BTree<T, E> T, String name, G g){
        bTree = T;
        this.g = g;
        this.setName(name);
        this.setSize(1400, 800);
        this.setLayout(new BorderLayout(10, 10));

        this.addComponentListener(new ComponentAdapter() {
           @Override
           public void componentResized(ComponentEvent e) {
               super.componentResized(e);
               paintBoard.removeAll();
               paintBoard.repaint();
           }
        });

        addModule();

        this.setVisible(true);
    }

    private void addModule(){
        paintBoard = new PaintBoard();
        this.add(paintBoard, BorderLayout.CENTER);
        //paintBoard.setBounds(0, 0, 800, 600);

        setP1();
        this.add(p1, BorderLayout.SOUTH);
        //p1.setBounds(0, 620, 1000, 180);

        setP2();
        this.add(p2, BorderLayout.EAST);
        p2.setSize(100, 600);
        //p2.setBounds(820, 0, 180, 600);
    }

    private void setP1(){
        JLabel  setM = new JLabel("B树阶数");
        JLabel key = new JLabel("关键字");
        JTextField M = new JTextField(10);
        JTextField K = new JTextField(10);
        JButton confirm = new JButton("确认");
        JButton insert = new JButton("插入");
        JButton delete = new JButton("删除");
        JButton search = new JButton("查找");

        confirm.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintBoard.removeAll();
                paintBoard.repaint();
                int k = Integer.parseInt(M.getText());
                bTree.setM(k);
                textArea.append(bTree.returnData);
                bTree.returnData = "";
                paintBoard.repaint();
            }
        });

        insert.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintBoard.removeAll();
                paintBoard.repaint();
                T t = g.createT(K.getText());
                bTree.insert(bTree.getRoot(), t);
                textArea.append(bTree.returnData);
                bTree.returnData = "";
                K.setText("");
                paintBoard.repaint();
            }
        });

        delete.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintBoard.removeAll();
                paintBoard.repaint();
                T t = g.createT(K.getText());
                bTree.delete(bTree.getRoot(), t);
                textArea.append(bTree.returnData);
                bTree.returnData = "";
                K.setText("");
                paintBoard.repaint();
            }
        });

        search.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paintBoard.removeAll();
                paintBoard.repaint();
                T t = g.createT(K.getText());
                bTree.search(bTree.getRoot(), t, 1);
                String s;
                if (bTree.flag) s = "查找成功\n";
                else s = "查找失败\n";
                textArea.append(s);
                K.setText("");
                paintBoard.repaint();
            }
        });

        p1 = new JPanel(new GridLayout(2, 1));
        JPanel temp1 = new JPanel(new FlowLayout()), temp2 = new JPanel(new FlowLayout());

        temp1.add(setM);
        temp1.add(M);
        temp1.add(confirm);

        temp2.add(key);
        temp2.add(K);
        temp2.add(insert);
        temp2.add(delete);
        temp2.add(search);

        p1.add(temp1);
        p1.add(temp2);
    }

    private void setP2(){
        p2 = new JPanel();
        textArea = new JTextArea();
        p2.add(textArea);
    }

    //画板类
    class PaintBoard extends JPanel{
        PaintBoard() {}

        public void paintComponent(Graphics g){

            //初始化
            super.paintComponent(g);
            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();

            //建立存储容器
           BTreeNode<T> baseNode = bTree.getRoot(), node;
           Queue<BTreeNode<T>> tempQueue = new ArrayDeque<>();
           Vector<BTreeNode<T>> tempArray = new Vector<>();
           Vector<Integer> inX = new Vector<>();
           Vector<Integer> inY = new Vector<>();

           //初始化输出队列
           tempQueue.add(baseNode);
           int tempY = y;
           int tempX;
           int n = 1;

           while(!tempQueue.isEmpty()){

               //在开始绘制每一层前都需获取初始坐标
               int dap = width/(n+6);
               tempX = x + dap;
               tempY += 100;

               while(!tempQueue.isEmpty()){
                   node = tempQueue.remove();

                   //先绘制连接线
                   if (!inX.isEmpty() && !inY.isEmpty()){
                       int x1 = inX.remove(0);
                       int y1 = inY.remove(0);
                       g.drawLine(x1, y1, tempX, tempY);
                   }

                   int i = 0;
                   //再绘制每个关键字
                   for(T data : node.key){
                       //绘制关键字区域
                       JButton jButton = new JButton(data.toString());
                       this.add(jButton);
                       jButton.setBounds(tempX, tempY, 60, 20);

                       //存储坐标
                       if (node.pointer.get(i) != null){
                           tempArray.add(node.pointer.get(i));
                           inX.add(tempX);
                           inY.add(tempY+20);
                       }
                       i++;
                       //每个关键字所占区域为60，因此下一个相邻关键字的X坐标应加60
                       tempX += 60;
                   }

                   //因为孩子数总比关键字个数多1，所以需单独将最后一个坐标加入
                   if (i != 0 && i < node.pointer.size()&& node.pointer.get(i) != null){
                       tempArray.add(node.pointer.get(i));
                       inX.add(tempX);
                       inY.add(tempY+20);
                   }

                   //每绘制完一个结点,需空出一定长度，以此区分每个结点
                   tempX += dap;
               }

               while(!tempArray.isEmpty()){
                   //将临时数组中的结点转移到输出队列中
                   tempQueue.add(tempArray.remove(0));
               }

               n = tempQueue.size();
           }

        }

    }
}


