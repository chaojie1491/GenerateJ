using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Text;
using System.Web.UI.WebControls;
using  ${packageName }.Core.Utility;
using  ${packageName }.Model.${model};
using  ${packageName }.Web;
using  ${packageName }.Application.Base;

public partial class  ${className }_Main : MainModuleBase
{
    protected void Page_Load(object sender, EventArgs e)
    {
    }

    protected override void OnInit(EventArgs e)
    {
        base.OnInit(e);
        ucNew.Back += NewBack_Render;
        ucNew.Create += CreateBack_Render;
        ucEdit.Back += EditBack_Render;
    }

    protected void GV_List_RowDataBound(object sender, GridViewRowEventArgs e)
    {
        if (e.Row.RowType == DataControlRowType.DataRow)
        {
        }
    }

    private void NewBack_Render(object sender, EventArgs e)
    {
        fldSearch.Visible = true;
        fldList.Visible = true;
        btnSearch_Click(null, null);
        ucNew.Visible = false;
    }

    private void CreateBack_Render(object sender, EventArgs e)
    {
        if (sender.ToString() == string.Empty)
        {
            fldSearch.Visible = true;
            fldList.Visible = true;
            btnSearch_Click(null, null);
            ucEdit.Visible = false;
            ucNew.Visible = false;
        }
        else
        {
            fldSearch.Visible = false;
            fldList.Visible = false;
            ucNew.Visible = false;
            ucEdit.Visible = true;
            ucEdit.InitPageParameter(( ${className })sender);
        }
    }

    private void EditBack_Render(object sender, EventArgs e)
    {
        fldSearch.Visible = true;
        fldList.Visible = true;
        btnSearch_Click(null, null);
        ucEdit.Visible = false;
    }

    protected void btnSearch_Click(object sender, EventArgs e)
    {
       try
       {
        var sqlParam = new List<SqlParameter>();
        var str = new StringBuilder(" select ");
        if (sender != btnExport)
        {
            str.Append(" top 1000 ");
        }
        else
        {
            str.Append(" top 10000 ");
        }
        <#assign listItem=""/>
        <#list fields as field>
            <#if field.columnName != "">
                ${listItem + "a." + field.columnName +  " [" + field.fieldDesc + "],"}
            </#if>
        </#list>

        str.AppendFormat("${listItem}");
        str.AppendFormat(@" from <%=TableName%> a
                        where 1=1 ");

        <%
        foreach (MappingInfo mappingInfo in this.Mapping.MappingInfoCollection)
        {
            if (mappingInfo.IsSearch)
        	{
                if (mappingInfo.PKGenerator=="identity")
                {
                    continue;
                }
                if (mappingInfo.DataType == "Boolean")
            	{
                %>
        str.Append(" and a.<%=mappingInfo.ClassPropertyName%> =@p<%=mappingInfo.ClassPropertyName%> ");
        sqlParam.Add(new SqlParameter("@p<%=mappingInfo.ClassPropertyName%>", this.cb<%=mappingInfo.ClassPropertyName%>.Checked));
            	<%
            	}
                else
            	{
                 %>
                tb<%=mappingInfo.ClassPropertyName%>.Text = tb<%=mappingInfo.ClassPropertyName%>.Text.Trim();
                if (tb<%=mappingInfo.ClassPropertyName%>.Text != string.Empty)
                {
        str.Append(" and a.<%=mappingInfo.ClassPropertyName%>=@p<%=mappingInfo.ClassPropertyName%> ");
        sqlParam.Add(new SqlParameter("@p<%=mappingInfo.ClassPropertyName%>", tb<%=mappingInfo.ClassPropertyName%>.Text));
                }
                <%
                    if(mappingInfo.ClassPropertyName=="FactoryCode")
                    {
                %>
                else
                {
                    throw new BusinessErrorException("请输入工厂");
                }
            	<%
                    }
            	}
            }
        }
        %>

        DataSet ds = TheGenericMgr.GetDatasetBySql(str.ToString(), sqlParam.ToArray());
        if (sender == btnExport)
        {
            ExcelHelper.ExportExcel(ds, string.Format(" ${className }_{0}.xls", DateTime.Now.ToString("MMdd_HHmm")), " ${className }");
        }
        else
        {
            GV_List.DataSource = ds;
            GV_List.DataBind();
            fldList.Visible = true;
        }
        ucEdit.Visible = false;
      }
      catch (Exception ex)
      {
        ShowErrorMessage(ex);
      }
    }

    protected void btnNew_Click(object sender, EventArgs e)
    {
        fldSearch.Visible = false;
        fldList.Visible = false;
        ucNew.Visible = true;
        ucNew.PageCleanup();
    }

    protected void lbtnEdit_Click(object sender, EventArgs e)
    {
        string[] paramKeys = ((LinkButton)sender).CommandArgument.Split(',');

        fldSearch.Visible = false;
        fldList.Visible = false;
        ucEdit.Visible = true;
        ucEdit.InitPageParameter(paramKeys);
    }
}
