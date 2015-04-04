package advisor.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import method.advisor.ClassNode;
import method.advisor.MethodNode;
import method.advisor.NodeDependence;
import method.japa.parser.JavaParser;
import method.japa.parser.ParseException;
import method.japa.parser.ast.CompilationUnit;
import method.japa.parser.ast.body.MethodDeclaration;
import method.japa.parser.ast.body.VariableDeclarator;
import method.japa.parser.ast.expr.AssignExpr;
import method.japa.parser.ast.expr.Expression;
import method.japa.parser.ast.expr.MethodCallExpr;
import method.japa.parser.ast.expr.VariableDeclarationExpr;
import method.japa.parser.ast.visitor.VoidVisitorAdapter;

public class MethodAdvisor {
	
		private List<ClassNode> classNodes = new ArrayList<ClassNode>();

		public static void main(String[] args) throws Exception {
			// creates an input stream for the file to be parsed
			String javaFileName = "classes/Test2.java";
			runMethodAdvisor(javaFileName);
		}

		public static void runMethodAdvisor(List<String> classes) {
				int count = 0;
				for (String javaFile : classes) {
						ClassNode cn;
						try {
							cn = runMethodAdvisor(javaFile);
							count += cn.getGranularity();
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				System.out.println("\n**********************************************");
				System.out.println("Total Method's Granularity: " + count);
		}

		
		private static ClassNode runMethodAdvisor(String javaFileName) throws FileNotFoundException,
				ParseException, IOException {
			FileInputStream in = new FileInputStream(javaFileName);
			CompilationUnit cu;
			try {
				// parse the file
				cu = JavaParser.parse(in);
			} finally {
				in.close();
			}

			// visit and print the methods names
			String packageOfClass = cu.getPackage().getName().getName();
			ClassNode classNode = new ClassNode(javaFileName, packageOfClass);
			
			new MethodVisitor().visit(cu, classNode);
			
			int totalGranularity = 0;
			for (MethodNode m : classNode.getMethodNodes()) {
				m.runClustering();
				int granularity = m.getClusterDependences().size() == 1 || m.getClusterDependences().size() == 0 ? 0 : m.getClusterDependences().size();
				totalGranularity += granularity;
				m.setGranularity(granularity);
			}
			
//			System.out.println("Class File: " + javaFileName);
//			System.out.println("Package: " + packageOfClass);
//			System.out.println("Class Granularity: " + totalGranularity +"\n");
			
			classNode.setGranularity(totalGranularity);
			for (MethodNode m : classNode.getMethodNodes()) {
//				System.out.println("Root Method: " + m.getRootName());
//				System.out.println("Granularity: " + m.getGranularity());
		
				System.out.println(m);
			}
			
			return classNode;
		}

		private static class MethodCallVisitor extends VoidVisitorAdapter {
			@Override
			public void visit(MethodCallExpr n, Object arg) {
					int line = (((MethodNode) arg).getStartLine() + n.getEndLine()) - 1;
					if (n.getArgs() != null && !n.getArgs().isEmpty()) {
						NodeDependence node = new NodeDependence(false, convert(n.getArgs()), n.getName(), line);
						((MethodNode) arg).getCallNodes().add(node);
					}else{
						NodeDependence node = new NodeDependence(false,  new ArrayList<String>(), n.getName(), line);
						((MethodNode) arg).getCallNodes().add(node);
					}
				}
			private List<String> convert(List<Expression> list) {
				List<String> mylist = new ArrayList<String>();
				for (Expression expression : list) {
					mylist.add(expression.toString());
				}
				return mylist;
			}
		}
		
		private static class ExpAssignVisitor extends VoidVisitorAdapter {
			@Override
			public NodeDependence visit(AssignExpr n, Object arg) {
				
				Expression value = n.getValue();
				MethodCallExpr mcexp = null;
				if (value instanceof MethodCallExpr) {
					mcexp = (MethodCallExpr) value;
					
					if (mcexp.getArgs() == null || mcexp.getArgs().isEmpty()) {
						return null;
					}
					int line = (((MethodNode) arg).getStartLine() + n.getEndLine()) - 1;
					NodeDependence node = new NodeDependence(true, Arrays.asList(n.getTarget().toString()), mcexp.getName(), line);
					((MethodNode) arg).getAssignNodes().add(node);
					return node;
				}
				return null;
			}
		}

		private static class VariableDeclarationExprVisitor extends VoidVisitorAdapter {
			@Override
			public NodeDependence visit(VariableDeclarationExpr n, Object arg) {
				int line = (((MethodNode) arg).getStartLine() + n.getEndLine()) - 1;
				if (n.getVars() != null && n.getVars().get(0) instanceof VariableDeclarator) {
					VariableDeclarator vd = (VariableDeclarator) n.getVars().get(0);
					if(vd.getInit() instanceof MethodCallExpr){
						MethodCallExpr mc = (MethodCallExpr) vd.getInit();
						NodeDependence node = new NodeDependence(true, Arrays.asList(vd.getId().toString()), mc.getName(), line);
						((MethodNode) arg).getAssignNodes().add(node);
						return node;
					}
				}
				
				return null;
			}
		}
		
		
		/**
		 * Simple visitor implementation for visiting MethodDeclaration nodes.
		 */
		private static class MethodVisitor extends VoidVisitorAdapter {

			@Override
			public void visit(MethodDeclaration n, Object arg) {
				CompilationUnit cu = null;

				
				try {
					String nomeArqTem = "temp.txt";
					File file = new File(nomeArqTem);
					if (!file.exists()) {
						file.createNewFile();
					} else {
						RandomAccessFile arquivo = new RandomAccessFile(
								nomeArqTem, "rw");

						// vamos excluir todo o conteúdo do arquivo
						arquivo.setLength(0);

						arquivo.close();
					}
					FileWriter fw = new FileWriter(file);
					fw.write("public class Temp{");
					fw.write(n.toString());
					fw.write("}");
					fw.flush();

					cu = JavaParser.parse(file);
					
//					System.out.println("é o que estamos procurando?");
//					System.out.println(cu);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MethodNode methodNode = new MethodNode(n, ((ClassNode) arg).getFileName(), ((ClassNode) arg).getJavaPackage());
				((ClassNode) arg).getMethodNodes().add(methodNode);

				new MethodCallVisitor().visit(cu, methodNode);
				new ExpAssignVisitor().visit(cu, methodNode);
				new VariableDeclarationExprVisitor().visit(cu, methodNode);
				
				// here you can access the attributes of the method.
				// this method will be called for all methods in this
				// CompilationUnit, including inner class methods
			}
		}
	}
