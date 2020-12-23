using System;
using <${ packageName}.Core.Model;

namespace ${packageName }.Model.${ model}
{
[Serializable]
public partial class ${ className} ${ extendsClass}
{
#region O/R Mapping Properties
<#list fields as field>
    /// <summary>
    /// ${field.fieldDesc}
    /// </summary>
    public ${ field.genType} ${ field.columnName}
    { get; set; }

</#list>
#endregion

}

}
