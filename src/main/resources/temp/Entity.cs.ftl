using System;
using He3.Core.Model;

namespace ${packageName}
{
[Serializable]
public partial class ${className} ${extendsClass}
{

<#list fields as field>
    /// <summary>
    /// ${field.fieldDesc}
    /// </summary>
    public ${field.genType} ${field.columnName} { get; set; }

</#list>


}

}
