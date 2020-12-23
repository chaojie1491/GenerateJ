using System;
using System.Linq;
using System.Web;
using System.Collections;
using log4net;
using He3.Model.LPP;
using He3.Web;
using He3.Application.Base;


public partial class ItemEcn_Edit : ModuleBase
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
        lbId.Text = entity.Id.ToString();
        tbEcnCode.Text = entity.EcnCode;
        tbBom.Text = entity.Bom;
        tbNewItem.Text = entity.NewItem;
        tbOldItem.Text = entity.OldItem;
        cbIsActive.Checked = entity.IsActive;
        tbLastModifyDate.Text = entity.LastModifyDate.ToString("yyyy-MM-dd HH:mm");
        tbLastModifyUser.Text = entity.LastModifyUser;
    }

    public void InitPageParameter(ItemEcn entity)
    {
    
        this.paramKeys = new object[] { entity.Id };
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
            
            tbEcnCode.Text = tbEcnCode.Text.Trim();
            entity.EcnCode = tbEcnCode.Text;
            tbBom.Text = tbBom.Text.Trim();
            entity.Bom = tbBom.Text;
            tbNewItem.Text = tbNewItem.Text.Trim();
            entity.NewItem = tbNewItem.Text;
            tbOldItem.Text = tbOldItem.Text.Trim();
            entity.OldItem = tbOldItem.Text;
             entity.IsActive = cbIsActive.Checked;
            tbLastModifyDate.Text = tbLastModifyDate.Text.Trim();
            entity.LastModifyDate = Convert.ToDateTime(tbLastModifyDate.Text);
            tbLastModifyUser.Text = tbLastModifyUser.Text.Trim();
            entity.LastModifyUser = tbLastModifyUser.Text;
            
            TheGenericMgr.Update(entity, this.CurrentUser.Code);
            ShowSuccessMessage("更新成功");
            InitPageParameter(paramKeys);
        }
        catch (Exception ex)
        {
            ShowErrorMessage(ex);
        }
    }

    private ItemEcn LoadEntity(object[] paramKeys)
    {
        var list = TheGenericMgr.FindAll<ItemEcn>(" from ItemEcn  where 1=1  and Id=?  ", paramKeys);
        if (list != null && list.Any())
        {
            return list[0];
        }
        return null;
    }
}
