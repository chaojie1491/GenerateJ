using System;
using System.Linq;
using System.Web;
using System.Collections;
using log4net;
using ${packageName}.Model.${model};
using ${packageName}.Web;
using ${packageName}.Application.Base;


public partial class ${className}_Edit : ModuleBase
{
    public event EventHandler Back;
    private object[] paramKeys
    {
        get
        {
            return (object[])ViewState[this.Page.ClientQueryString + "paramKeys"];
        }
        set
        {
            ViewState[this.Page.ClientQueryString + "paramKeys"] = value;
        }
    }

    protected void Page_Load(object sender, EventArgs e)
    {
        if (!IsPostBack)
        {
        }
    }

    public void InitPageParameter(object[] paramKeys)
    {
        this.paramKeys = paramKeys;
        var entity = LoadEntity(paramKeys);
        <#list fields as field>
            <#if field.genType == "Boolean">
                <#if field.isNull == "Y">
                    cb${field.columnName}.Checked = entity.<%=mappingInfo.ClassPropertyName%> ?? false;
                <#else>
                    cb${field.columnName}.Checked = entity.<%=mappingInfo.ClassPropertyName%>
                </#if >
            <#elseif field.genType == "String">
               tb${field.columnName}.Text = tb${field.columnName}.Text.Trim();
                entity.${field.columnName} = tb${field.columnName}.Text;
            <#elseif field.genType == "DateTime">
                tb${field.columnName}.Text = tb${field.columnName}.Text.Trim();
                entity.${field.columnName} = Convert.ToDateTime(tb${field.columnName}.Text);
            <#else >
                entity." + mappingInfo.ClassPropertyName + @".ToString()"
            </#if>

        </#list>
    }

    public void InitPageParameter(${className} entity)
    {
        <% 
        var paramKeys = string.Empty;
        foreach (MappingInfo mappingInfo in Mapping.MappingInfoCollection)
        {
            if (mappingInfo.IsPK)
        	{
        		paramKeys += string.Format(@"entity.{0},", mappingInfo.ClassPropertyName);
        	}
        } 
        paramKeys= paramKeys.Substring(0,paramKeys.Length - 1);
        %>
    
        this.paramKeys = new object[] { <%=paramKeys%> };
        InitPageParameter(this.paramKeys);
    }

    protected void btnBack_Click(object sender, EventArgs e)
    {
        if (Back != null)
        {
            Visible = false;
            Back(sender, e);
        }
    }

    protected void btnUpdate_Click(object sender, EventArgs e)
    {
        try
        {
            var entity = LoadEntity(paramKeys);
            
            <% 
            foreach (MappingInfo mappingInfo in this.Mapping.MappingInfoCollection) 
            {
                if(!mappingInfo.IsPK && mappingInfo.IsEdit)
                {
                    if (mappingInfo.DataType != "Boolean")
                	{
                    %>
            tb<%=mappingInfo.ClassPropertyName%>.Text = tb<%=mappingInfo.ClassPropertyName%>.Text.Trim();
                        <% 
                        if(mappingInfo.DataType != "String")
                        {
                        %>
            entity.<%=mappingInfo.ClassPropertyName%> = Convert.To<%=mappingInfo.DataType%>(tb<%=mappingInfo.ClassPropertyName%>.Text);
                        <%
                        }
                        else
                    	{
                        %>
            entity.<%=mappingInfo.ClassPropertyName%> = tb<%=mappingInfo.ClassPropertyName%>.Text;
                    <%
                    	}
                	}
                    else
                	{
                        %>
             entity.<%=mappingInfo.ClassPropertyName%> = cb<%=mappingInfo.ClassPropertyName%>.Checked;
                       <%
                	}
                }
            } 
            %>
            
            TheGenericMgr.Update(entity, this.CurrentUser.Code);
            ShowSuccessMessage("更新成功");
            InitPageParameter(paramKeys);
        }
        catch (Exception ex)
        {
            ShowErrorMessage(ex);
        }
    }

    private ${className} LoadEntity(object[] paramKeys)
    {
        <%
        string wheresql = " where 1=1 ";
        foreach (MappingInfo mappingInfo in this.Mapping.MappingInfoCollection) 
        {
            if (mappingInfo.IsPK)
        	{
        		wheresql+= " and " + mappingInfo.ClassPropertyName + "=? ";
        	}
        }
        %>
        var list = TheGenericMgr.FindAll<${className}>(" from ${className} <%=wheresql%> ", paramKeys);
        if (list != null && list.Any())
        {
            return list[0];
        }
        return null;
    }
}
