// PREVIOUS APPROACH - NOT USED ANYMORE

//package pl.poznan.put.kacperwleklak.creek.sql;
//
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.LongValue;
//import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
//import net.sf.jsqlparser.expression.operators.relational.ItemsList;
//import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
//import net.sf.jsqlparser.expression.operators.relational.NamedExpressionList;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.parser.JSqlParser;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.schema.Table;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.StatementVisitor;
//import net.sf.jsqlparser.statement.Statements;
//import net.sf.jsqlparser.statement.create.table.CreateTable;
//import net.sf.jsqlparser.statement.insert.Insert;
//import net.sf.jsqlparser.statement.select.Select;
//import net.sf.jsqlparser.statement.select.SubSelect;
//import net.sf.jsqlparser.statement.update.Update;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class SqlTransformer {
//
//    private static final String VERSION_COL_NAME = "version";
//    private static final int VERSION_COL_IDX = 1;
//
//    public static String transform(String SQL, long versionId) throws JSQLParserException {
//        Statements statements = CCJSqlParserUtil.parseStatements(SQL);
//        statements.getStatements()
//                .stream()
//                .map();
//    }
//
//    private static Statement transform(Statement statement, long versionId) {
//        if (statement instanceof Insert) {
//            transformInsert((Insert) statement, versionId);
//        } else if (statement instanceof Update) {
//
//        } else if (statement instanceof Select) {
//            select((Select) statement);
//        } else {
//
//        }
//        return new Statement() {
//            @Override
//            public void accept(StatementVisitor statementVisitor) {
//
//            }
//        };
//    }
//
//    private static Statement transformInsert(Insert insertStatement, long versionId) {
//        List<Column> columns = insertStatement.getColumns();
//        Table table = insertStatement.getTable();
//        if (!columns.isEmpty()) {
//            columns.add(VERSION_COL_IDX, new Column(table, VERSION_COL_NAME));
//            insertStatement.getItemsList().
//        }
//    }
//
//    private static ItemsList transformItemsList(ItemsList itemsList, long versionId) {
//        if (itemsList instanceof SubSelect) {
//            SubSelect subSelect = (SubSelect) itemsList;
//            subSelect.get
//        } else if (itemsList instanceof ExpressionList) {
//            ExpressionList expressionList = (ExpressionList) itemsList;
//            expressionList.getExpressions().add(VERSION_COL_IDX, new LongValue(versionId));
//            return expressionList;
//        } else if (itemsList instanceof NamedExpressionList) {
//            throw new UnsupportedOperationException();
//        } else if (itemsList instanceof MultiExpressionList) {
//            throw new UnsupportedOperationException();
//        } else {
//            throw new UnsupportedOperationException();
//        }
//    }
//}
