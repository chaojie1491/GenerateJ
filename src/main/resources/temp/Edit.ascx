<%@ Control Language="C#" AutoEventWireup="true" CodeFile="Edit.ascx.cs" Inherits="ItemEcn_Edit" %>
<%@ Register Src="~/WebForms/Controls/TextBox.ascx" TagName="textbox" TagPrefix="uc3" %>
<%@ Register Assembly="He3.Core" Namespace="He3.Core.Control" TagPrefix="cc1" %>

<fieldset>
    <legend>编辑</legend>
    <table class="mtable">
    <tr>
        <td class="td01">Id:</td>
        <td class="td02">
         <asp:Label ID="lbId" runat="server" />
        </td>            
        <td class="td01">Ecn号:</td>
        <td class="td02">
         <asp:TextBox ID="tbEcnCode" runat="server" CssClass="inputRequired"  MaxLength="50" />
         <asp:RequiredFieldValidator ID="rfvEcnCode" runat="server" ControlToValidate="tbEcnCode"
              Display="Dynamic" ErrorMessage="不允许为空" ValidationGroup="vgSave" ForeColor="Red"/>
        </td>            
         </tr>
         <tr>
        <td class="td01">成品:</td>
        <td class="td02">
         <asp:TextBox ID="tbBom" runat="server" CssClass="inputRequired"  MaxLength="50" />
         <asp:RequiredFieldValidator ID="rfvBom" runat="server" ControlToValidate="tbBom"
              Display="Dynamic" ErrorMessage="不允许为空" ValidationGroup="vgSave" ForeColor="Red"/>
        </td>            
        <td class="td01">新物料号:</td>
        <td class="td02">
         <asp:TextBox ID="tbNewItem" runat="server" CssClass="inputRequired"  MaxLength="50" />
         <asp:RequiredFieldValidator ID="rfvNewItem" runat="server" ControlToValidate="tbNewItem"
              Display="Dynamic" ErrorMessage="不允许为空" ValidationGroup="vgSave" ForeColor="Red"/>
        </td>            
         </tr>
         <tr>
        <td class="td01">老物料号:</td>
        <td class="td02">
         <asp:TextBox ID="tbOldItem" runat="server" CssClass="inputRequired"  MaxLength="50" />
         <asp:RequiredFieldValidator ID="rfvOldItem" runat="server" ControlToValidate="tbOldItem"
              Display="Dynamic" ErrorMessage="不允许为空" ValidationGroup="vgSave" ForeColor="Red"/>
        </td>            
        <td class="td01">有效:</td>
        <td class="td02">
        <asp:CheckBox ID="cbIsActive" runat="server" Enabled="True"/>
        </td>            
         </tr>
         <tr>
        <td class="td01">最后修改时间:</td>
        <td class="td02">
         <asp:TextBox ID="tbLastModifyDate" runat="server" CssClass="inputRequired" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})"  />
         <asp:RequiredFieldValidator ID="rfvLastModifyDate" runat="server" ControlToValidate="tbLastModifyDate"
              Display="Dynamic" ErrorMessage="不允许为空" ValidationGroup="vgSave" ForeColor="Red"/>
        </td>            
        <td class="td01">最后修改用户:</td>
        <td class="td02">
         <asp:TextBox ID="tbLastModifyUser" runat="server" CssClass="inputRequired"  MaxLength="50" />
         <asp:RequiredFieldValidator ID="rfvLastModifyUser" runat="server" ControlToValidate="tbLastModifyUser"
              Display="Dynamic" ErrorMessage="不允许为空" ValidationGroup="vgSave" ForeColor="Red"/>
        </td>            
         </tr>
         <tr>
    </tr>
    </table>
    <div class="tablefooter">
        <cc1:Button id="btnUpdate" runat="server" Text="保存" OnClick="btnUpdate_Click" FunctionId="Page_MD_Edit" ValidationGroup="vgSave" />
        <asp:Button ID="btnBack" runat="server" Text="返回" OnClick="btnBack_Click" />
    </div>
</fieldset>
